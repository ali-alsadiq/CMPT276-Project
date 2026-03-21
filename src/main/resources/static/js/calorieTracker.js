// Progress Circle Animation
document.querySelectorAll(".progress-circle").forEach(circle => {
    const value = circle.style.getPropertyValue("--value").trim();
    const text = circle.querySelector(".progress-value, .progress-value-small");
    if (text) text.textContent = `${value}%`;
});

// Helper for safe event listeners
const addSafeListener = (id, event, callback) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener(event, callback);
};

// ADD MEAL / SEARCH PANEL (Legacy or optional)
addSafeListener("add-meal-btn", "click", () => {
    const panel = document.getElementById("meal-search-panel");
    if (panel) panel.classList.toggle("active");
});

addSafeListener("meal-search-btn", "click", async () => {
    const input = document.getElementById("meal-search-input");
    const results = document.getElementById("meal-search-results");
    if (!input || !results) return;

    const query = input.value.trim();
    if (!query) return;

    try {
        results.innerHTML = `<p class="meal-result-meta">Analyzing meal...</p>`;
        const response = await fetch(`/api/nutrition?query=${encodeURIComponent(query)}`);

        if (!response.ok) throw new Error("Failed to fetch nutrition data");

        const foods = await response.json();

        if (!foods || foods.length === 0) {
            results.innerHTML = `<p class="meal-result-meta">No foods found.</p>`;
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
        results.innerHTML = html;
    } catch (error) {
        console.error(error);
        results.innerHTML = `<p class="meal-result-meta">Something went wrong while fetching nutrition data.</p>`;
    }
});
