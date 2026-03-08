

// Progress Circle Animation
document.querySelectorAll(".progress-circle").forEach(circle => {
    const value = circle.style.getPropertyValue("--value").trim();
    const text = circle.querySelector(".progress-value");
    if (text) text.textContent = `${value}%`;
});


// ADD MEAL
const addMealBtn = document.getElementById("add-meal-btn");
const mealSearchPanel = document.getElementById("meal-search-panel");

addMealBtn.addEventListener("click", () => {
    mealSearchPanel.classList.toggle("active");
});


const mealSearchBtn = document.getElementById("meal-search-btn");
const mealSearchInput = document.getElementById("meal-search-input");
const mealSearchResults = document.getElementById("meal-search-results");

mealSearchBtn.addEventListener("click", async () => {
  const query = mealSearchInput.value.trim();

  if (!query) return;

  // call api here:
  // const response = await fetch(`/api/meals?query=${encodeURIComponent(query)}`);
  // const meals = await response.json();

  mealSearchResults.innerHTML = `
    <div class="meal-result-item">
      <img src="" alt="Meal image" class="meal-result-image">
      <div class="meal-result-info">
        <p class="meal-result-name">${query}</p>
        <p class="meal-result-meta">400 cal • 12g fat • 35g carbs</p>
      </div>
      <button class="meal-result-add-btn">Add</button>
    </div>
  `;
});