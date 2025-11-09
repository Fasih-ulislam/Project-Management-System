document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

    const buttons = document.querySelectorAll('.nav-button');
    const forms = document.querySelectorAll('.form-section');
  
    const urlParams = new URLSearchParams(window.location.search);
    const projectId = urlParams.get("projectId");
    const taskId = urlParams.get("taskId");
  
    if (!projectId || !taskId) {
      alert("Missing project or task ID in URL.");
      return;
    }
  
    function showForm(formId) {
      forms.forEach(form => {
        form.style.display = (form.id === formId) ? 'block' : 'none';
      });
      buttons.forEach(btn => {
        btn.classList.toggle('active', btn.dataset.target === formId);
      });
    }
  
    showForm('info-form');
    fetchExpenses();
  
    function fetchExpenses() {
      fetch(`http://localhost:8080/projects/${projectId}/tasks/${taskId}/expenses`, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      })
      .then(res => res.json())
      .then(expenses => {
        const container = document.getElementById("expense-list");
        container.innerHTML = "";
        if (expenses.length === 0) {
          container.innerHTML = "<p>No expenses found.</p>";
          return;
        }
  
        expenses.forEach(exp => {
          const card = document.createElement("div");
          card.className = "expense-card";
          card.innerHTML = `
            <p><strong>Description:</strong> ${exp.description}</p>
            <p><strong>Amount:</strong> $${exp.amount}</p>
          `;
          card.addEventListener("click", () => {
            document.getElementById("update-expense-id").value = exp.id;
            document.getElementById("delete-expense-id").value = exp.id;
          });
          container.appendChild(card);
        });
      })
      .catch(err => {
        console.error(err);
        alert("Error fetching expenses.");
      });
    }
  
    // Create
    document.getElementById("expense-create-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const form = e.target;
      const expenseData = {
        description: form.description.value,
        amount: parseFloat(form.amount.value),
      };
  
      try {
        const res = await fetch(`http://localhost:8080/projects/${projectId}/tasks/${taskId}/expenses`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          body: JSON.stringify(expenseData),
        });
        if (!res.ok) throw new Error("Create failed");
        alert("Expense created.");
        form.reset();
        showForm("info-form");
        fetchExpenses();
      } catch (err) {
        console.error(err);
        alert("Failed to create expense.");
      }
    });
  
    // Update
    document.getElementById("expense-update-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const id = document.getElementById("update-expense-id").value;
      const form = e.target;
      const expenseData = {
        description: form.description.value,
        amount: parseFloat(form.amount.value),
      };
  
      try {
        const res = await fetch(`http://localhost:8080/projects/${projectId}/tasks/${taskId}/expenses/${id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          body: JSON.stringify(expenseData),
        });
        if (!res.ok) throw new Error("Update failed");
        alert("Expense updated.");
        form.reset();
        showForm("info-form");
        fetchExpenses();
      } catch (err) {
        console.error(err);
        alert("Failed to update expense.");
      }
    });
  
    // Delete
    document.getElementById("expense-delete-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const id = document.getElementById("delete-expense-id").value;
  
      try {
        const res = await fetch(`http://localhost:8080/projects/${projectId}/tasks/${taskId}/expenses/${id}`, {
          method: "DELETE",
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        });
        if (!res.ok) throw new Error("Delete failed");
        alert("Expense deleted.");
        showForm("info-form");
        fetchExpenses();
      } catch (err) {
        console.error(err);
        alert("Failed to delete expense.");
      }
    });
  
    // Nav button handlers
    buttons.forEach(btn => {
      btn.addEventListener("click", () => showForm(btn.dataset.target));
    });
  });
  