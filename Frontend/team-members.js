document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

    const urlParams = new URLSearchParams(window.location.search);
    const teamId = urlParams.get("teamId");

    const myTeamContainer = document.getElementById("myTeamList");
    const availableMembersContainer = document.getElementById("availableMembersList");

    if (!teamId || !token) {
        alert("Missing team ID or token.");
        return;
    }

    async function loadMyTeam() {
        try {
            const res = await fetch(`http://localhost:8080/teams/${teamId}/members`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!res.ok) throw new Error("Failed to fetch team members");

            const members = await res.json();
            myTeamContainer.innerHTML = "";

            if (members.length === 0) {
                myTeamContainer.innerHTML = "<li>No members in this team.</li>";
                return;
            }

            members.forEach(member => {
                const li = document.createElement("li");
                li.innerHTML = `
                    <div class="member-info">
        <span><strong>ID:</strong> ${member.id}</span>
        <span><strong>Name:</strong> ${member.name}</span>
        <span><strong>Position:</strong> ${member.position}</span>
    </div>
                    <button class="action-btn">Remove</button>
                `;
                li.querySelector("button").addEventListener("click", () => removeMember(member.id));
                myTeamContainer.appendChild(li);
            });
        } catch (err) {
            console.error(err);
            alert("Error loading team members.");
        }
    }

    async function loadAvailableMembers() {
        try {
            const res = await fetch(`http://localhost:8080/members/all`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!res.ok) throw new Error("Failed to fetch all members");

            const allMembers = await res.json();

            const teamRes = await fetch(`http://localhost:8080/teams/${teamId}/members`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!teamRes.ok) throw new Error("Failed to fetch team members");

            const teamMembers = await teamRes.json();
            const teamMemberIds = teamMembers.map(m => m.id);

            const availableMembers = allMembers.filter(m => !teamMemberIds.includes(m.id));
            availableMembersContainer.innerHTML = "";

            if (availableMembers.length === 0) {
                availableMembersContainer.innerHTML = "<li>No members available to add.</li>";
                return;
            }

            availableMembers.forEach(member => {
                const li = document.createElement("li");
                li.innerHTML = `
    <div class="member-info">
        <span><strong>ID:</strong> ${member.id}</span>
        <span><strong>Name:</strong> ${member.name}</span>
        <span><strong>Position:</strong> ${member.position}</span>
    </div>
    <button class="action-btn">Add</button>
`;

                li.querySelector("button").addEventListener("click", () => addMember(member.id));
                availableMembersContainer.appendChild(li);
            });
        } catch (err) {
            console.error(err);
            alert("Error loading available members.");
        }
    }

    async function addMember(memberId) {
        const confirmed = confirm("Are you sure you want to add this member to the team?");
        if (!confirmed) return;

        try {
            const res = await fetch(`http://localhost:8080/teams/${teamId}/members/${memberId}`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!res.ok) throw new Error("Failed to add member");

            alert("Member added successfully!");
            loadMyTeam();
            loadAvailableMembers();
        } catch (err) {
            console.error(err);
            alert("Failed to add member.");
        }
    }

    async function removeMember(memberId) {
        const confirmed = confirm("Are you sure you want to remove this member from the team?");
        if (!confirmed) return;

        try {
            const res = await fetch(`http://localhost:8080/teams/${teamId}/members/${memberId}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (!res.ok) throw new Error("Failed to remove member");

            alert("Member removed successfully!");
            loadMyTeam();
            loadAvailableMembers();
        } catch (err) {
            console.error(err);
            alert("Failed to remove member.");
        }
    }

    // Navigation buttons
    document.getElementById("myTeamBtn").addEventListener("click", () => {
        document.getElementById("myTeamSection").classList.remove("hidden");
        document.getElementById("addMembersSection").classList.add("hidden");
    });

    document.getElementById("addMembersBtn").addEventListener("click", () => {
        document.getElementById("myTeamSection").classList.add("hidden");
        document.getElementById("addMembersSection").classList.remove("hidden");
    });

    // Initial load
    loadMyTeam();
    loadAvailableMembers();
});
