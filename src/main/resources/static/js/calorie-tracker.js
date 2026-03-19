// Progress Circle Animation
document.querySelectorAll(".progress-circle").forEach(circle => {
    const value = circle.style.getPropertyValue("--value").trim();
    const text = circle.querySelector(".progress-value");
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

// CHATBOT MESSAGE HANDLING
const appendMessage = (text) => {
    const chatHistory = document.getElementById("chat-history");
    if (!chatHistory) return;

    const messageWrapper = document.createElement('div');
    messageWrapper.className = 'd-flex justify-content-end w-100 chat-bubble-wrapper';

    const messageBubble = document.createElement('div');
    messageBubble.className = 'bg-accent-1 text-black rounded-4 px-3 py-2 shadow-sm';
    messageBubble.style.maxWidth = '85%';
    messageBubble.style.wordBreak = 'break-word';
    messageBubble.textContent = text;

    messageWrapper.appendChild(messageBubble);
    chatHistory.appendChild(messageWrapper);
    chatHistory.scrollTop = chatHistory.scrollHeight;
};

const activateChatMode = () => {
    const container = document.getElementById("logging-container");
    const input = document.getElementById("logging-input");
    if (!input || input.value.trim() === "") return;

    const messageText = input.value.trim();
    input.value = "";

    const isFirstMessage = !container.classList.contains("chat-mode");

    if (isFirstMessage) {
        const welcome = document.getElementById("welcome-section");

        // ONE trigger: adding chat-mode drives all CSS transitions simultaneously
        // - welcome fades + slides down (0.5s)
        // - spacers collapse (0.7s)
        // - chat-history expands (0.7s)
        container.classList.add("chat-mode");

        // Hide welcome from layout after its transition finishes
        if (welcome) {
            welcome.addEventListener("transitionend", () => {
                welcome.style.display = "none";
            }, { once: true });
        }

        // Append bubble after layout transitions complete (0.7s + small buffer)
        setTimeout(() => {
            appendMessage(messageText);
        }, 800);

    } else {
        appendMessage(messageText);
    }
};

// Listeners for sending the message
addSafeListener("send-meal-btn", "click", activateChatMode);

const loggingInput = document.getElementById("logging-input");
if (loggingInput) {
    loggingInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault(); // Stops Enter from making a new line in the textarea
            activateChatMode();
        }
    });
}