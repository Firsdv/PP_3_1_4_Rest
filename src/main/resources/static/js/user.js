document.addEventListener("DOMContentLoaded", async () => {
    const tableBody = document.getElementById("userTableBody");
    const userInfoBar = document.getElementById("user-info");

    try {
        const response = await fetch("/api/user");
        if (!response.ok) {
            throw new Error("Failed to fetch user data");
        }

        const user = await response.json();

        tableBody.innerHTML = `
            <tr>
                <td>${user.id}</td>
                <td>${user.firstname}</td>
                <td>${user.lastname}</td>
                <td>${user.email}</td>
                <td>${user.roles.map(r => r.name.replace("ROLE_", "")).join(", ")}</td>
            </tr>
        `;

        // Обновляем заголовок шапки
        userInfoBar.textContent = `${user.email} with roles: ${user.roles.map(r => r.name.replace("ROLE_", "")).join(", ")}`;

    } catch (error) {
        console.error("Error loading user data:", error);
        tableBody.innerHTML = `<tr><td colspan="5" class="text-danger">Failed to load user data</td></tr>`;
    }
});