// Progress Circle Animation
document.querySelectorAll(".progress-circle").forEach((circle) => {
  const value = circle.style.getPropertyValue("--value").trim();
  const text = circle.querySelector(".progress-value, .progress-value-small");
  if (text) text.textContent = `${value}%`;
});

// Modal + search elements
const openSearchModalBtn = document.getElementById("openSearchModalBtn");
const foodDescriptionInput = document.getElementById("foodDescription");
const modalFoodItemsContainer = document.getElementById(
  "modalFoodItemsContainer",
);
const modalFoodDescriptionPreview = document.getElementById(
  "modalFoodDescriptionPreview",
);
const addMealModalElement = document.getElementById("addMealModal");
const consumedDateInput = document.getElementById("consumedDate");

// Returns current local datetime in yyyy-MM-ddTHH:mm format
function getCurrentDateTimeLocal() {
  const now = new Date();
  const pad = (n) => String(n).padStart(2, "0");

  return (
    now.getFullYear() +
    "-" +
    pad(now.getMonth() + 1) +
    "-" +
    pad(now.getDate()) +
    "T" +
    pad(now.getHours()) +
    ":" +
    pad(now.getMinutes())
  );
}

// Escapes text before inserting into HTML
function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

// Builds the modal food list returned from /calorieTracker/search
function buildFoodItemsHtml(foods) {
  if (!foods || foods.length === 0) {
    return `<div class="text-muted">No foods found.</div>`;
  }

  let html = "";

  foods.forEach((food, index) => {
    const foodName = food.foodName ?? food.name ?? "Unknown food";
    const servSize = Number(food.servSize ?? 0);

    html += `
      <div class="border rounded p-3 mb-3">
        <div class="mb-3">
          <label class="form-label" for="foodNameDisplay${index}">Food Name</label>
          <input
            type="text"
            id="foodNameDisplay${index}"
            class="form-control"
            value="${escapeHtml(foodName)}"
            readonly
          />
        </div>

        <div class="mb-3">
          <label class="form-label" for="requestedServSize${index}">Serving Size (grams)</label>
          <input
            type="number"
            step="1"
            min="1"
            id="requestedServSize${index}"
            class="form-control"
            name="requestedServSizes[${escapeHtml(foodName)}]"
            value="${servSize > 0 ? servSize : 0.1}"
            required
          />
        </div>

        <input type="hidden" name="foodOrder" value="${escapeHtml(foodName)}" />
      </div>
    `;
  });

  return html;
}

// Sends POST request to search foods, then opens modal with results
async function searchFoodsAndOpenModal() {
  if (
    !foodDescriptionInput ||
    !modalFoodItemsContainer ||
    !addMealModalElement
  ) {
    return;
  }

  const query = foodDescriptionInput.value.trim();

  if (modalFoodDescriptionPreview) {
    modalFoodDescriptionPreview.value = query;
  }

  if (consumedDateInput && !consumedDateInput.value) {
    consumedDateInput.value = getCurrentDateTimeLocal();
  }

  if (!query) {
    modalFoodItemsContainer.innerHTML = `
      <div class="text-muted">Please enter a food search first.</div>
    `;
    bootstrap.Modal.getOrCreateInstance(addMealModalElement).show();
    return;
  }

  modalFoodItemsContainer.innerHTML = `
    <div class="text-muted">Searching foods...</div>
  `;

  try {
    const response = await fetch("/calorieTracker/search", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        Accept: "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
      body: new URLSearchParams({
        foodDescription: query,
      }),
    });

    if (!response.ok) {
      throw new Error("Search request failed");
    }

    const foods = await response.json();
    modalFoodItemsContainer.innerHTML = buildFoodItemsHtml(foods);
    bootstrap.Modal.getOrCreateInstance(addMealModalElement).show();
  } catch (error) {
    console.error(error);
    modalFoodItemsContainer.innerHTML = `
      <div class="text-danger">Something went wrong while searching foods.</div>
    `;
    bootstrap.Modal.getOrCreateInstance(addMealModalElement).show();
  }
}

// Open modal flow from the right arrow button
if (openSearchModalBtn) {
  openSearchModalBtn.addEventListener("click", function (event) {
    event.preventDefault();
    searchFoodsAndOpenModal();
  });
}
