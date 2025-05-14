const API_USERS = '/api/admin/users';
const API_ROLES = '/api/admin/roles';
const API_USER_INFO = '/api/user';

document.addEventListener('DOMContentLoaded', async () => {
    await loadAuthUser();
    await loadRoles();
    await loadUsers();

    document.getElementById('newUserForm').addEventListener('submit', addUser);
    document.getElementById('userForm').addEventListener('submit', updateUser);
});

async function loadAuthUser() {
    const res = await fetch(API_USER_INFO);
    const user = await res.json();
    const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
    document.getElementById('admin-info').textContent = `${user.email} with roles: ${roles}`;
}

async function loadUsers() {
    const res = await fetch(API_USERS);
    const users = await res.json();
    const tbody = document.getElementById('userTableBody');
    tbody.innerHTML = '';

    users.forEach(user => {
        const roles = user.roles.map(r => r.name.replace('ROLE_', '')).join(', ');
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstname}</td>
            <td>${user.lastname}</td>
            <td>${user.email}</td>
            <td>${roles}</td>
            <td><button class="btn btn-sm btn-primary" onclick="openEditModal(${user.id})">Edit</button></td>
            <td><button class="btn btn-sm btn-danger" onclick="openDeleteModal(${user.id})">Delete</button></td>
        `;
        tbody.appendChild(row);
    });
}

async function loadRoles() {
    const res = await fetch(API_ROLES);
    const roles = await res.json();

    const roleSelects = [document.getElementById('roles'), document.getElementById('editRoles')];
    roleSelects.forEach(select => {
        select.innerHTML = '';
        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.id;
            option.text = role.name.replace('ROLE_', '');
            select.appendChild(option);
        });
    });
}

async function addUser(e) {
    e.preventDefault();

    const newUser = {
        firstname: document.getElementById('firstname').value,
        lastname: document.getElementById('lastname').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        roles: Array.from(document.getElementById('roles').selectedOptions).map(o => ({ id: o.value }))
    };

    await fetch(API_USERS, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newUser)
    });

    await loadUsers();
    showTab('users');
    document.getElementById('newUserForm').reset();
}

async function openEditModal(id) {
    const res = await fetch(`${API_USERS}/${id}`);
    const user = await res.json();

    document.getElementById('userId').value = user.id;
    document.getElementById('editFirstname').value = user.firstname;
    document.getElementById('editLastname').value = user.lastname;
    document.getElementById('editEmail').value = user.email;
    document.getElementById('editPassword').value = '';

    const roleSelect = document.getElementById('editRoles');
    Array.from(roleSelect.options).forEach(option => {
        option.selected = user.roles.some(r => r.id == option.value);
    });

    document.getElementById('userModal').classList.add('show');
}

async function updateUser(e) {
    e.preventDefault();
    const id = document.getElementById('userId').value;

    const updatedUser = {
        id: id,
        firstname: document.getElementById('editFirstname').value,
        lastname: document.getElementById('editLastname').value,
        email: document.getElementById('editEmail').value,
        password: document.getElementById('editPassword').value,
        roles: Array.from(document.getElementById('editRoles').selectedOptions).map(o => ({ id: o.value }))
    };

    await fetch(`${API_USERS}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedUser)
    });

    closeModal();
    await loadUsers();
}

function closeModal() {
    document.getElementById('userModal').classList.remove('show');
}

async function openDeleteModal(id) {
    const res = await fetch(`${API_USERS}/${id}`);
    const user = await res.json();

    document.getElementById('deleteId').value = user.id;
    document.getElementById('deleteFirstName').value = user.firstname;
    document.getElementById('deleteLastName').value = user.lastname;
    document.getElementById('deleteEmail').value = user.email;

    const roleSelect = document.getElementById('deleteRoles');
    roleSelect.innerHTML = '';
    user.roles.forEach(role => {
        const option = document.createElement('option');
        option.textContent = role.name.replace('ROLE_', '');
        roleSelect.appendChild(option);
    });

    document.getElementById('deleteModal').classList.add('show');
}


function closeDeleteModal() {
    document.getElementById('deleteModal').classList.remove('show');
}

async function confirmDeleteUser() {
    const id = document.getElementById('deleteId').value;
    await fetch(`${API_USERS}/${id}`, { method: 'DELETE' });
    closeDeleteModal();
    await loadUsers();
}