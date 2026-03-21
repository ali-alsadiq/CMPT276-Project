// Progress Circle Animation
document.querySelectorAll(".progress-circle").forEach(circle => {
    const value = circle.style.getPropertyValue("--value").trim();
    const text = circle.querySelector(".progress-value, .progress-value-small");
    if (text) text.textContent = `${value}%`;
});


// ADD MEAL
const addMealBtn = document.getElementById("add-meal-btn");
const mealSearchPanel = document.getElementById("meal-search-panel");
const mealSearchBtn = document.getElementById("meal-search-btn");
const mealSearchInput = document.getElementById("meal-search-input");
const mealSearchResults = document.getElementById("meal-search-results");


addMealBtn.addEventListener("click", () => {
    mealSearchPanel.classList.toggle("active");
});

mealSearchBtn.addEventListener("click", async () => {
    const query = mealSearchInput.value.trim();
    if (!query) return;
    try {
        mealSearchResults.innerHTML = `<p class="meal-result-meta">Analyzing meal...</p>`;
        const response = await fetch(`/api/nutrition?query=${encodeURIComponent(query)}`);

        if (!response.ok) {
            throw new Error("Failed to fetch nutrition data");
        }

        const foods = await response.json();

        if (!foods || foods.length === 0) {
            mealSearchResults.innerHTML = `<p class="meal-result-meta">No foods found.</p>`;
            return;
        }

        let html = "";

        foods.forEach(food => {
            html += `
                <div class="meal-result-item">
                    <div class="meal-result-info">
                        <p class="meal-result-name">${food.name ?? "Unknown food"}</p>
                        <p class="meal-result-meta">
                            ${food.calories ?? 0} cal • ${food.fat_total_g ?? 0}g fat • ${food.carbohydrates_total_g ?? 0}g carbs
                        </p>
                    </div>
                    <button class="meal-result-add-btn" type="button">
                        <i class="fa fa-plus" aria-hidden="true"></i>
                    </button>
                </div>
            `;
        });

        mealSearchResults.innerHTML = html;
    } catch (error) {
        console.error(error);
        mealSearchResults.innerHTML = `<p class="meal-result-meta">Something went wrong while fetching nutrition data.</p>`;
    }
});
