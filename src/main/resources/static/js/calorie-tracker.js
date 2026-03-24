const GENERATING_PHRASES = [
    "Consulting my nutrition database...",
    "Crunching the macro numbers...",
    "Calculating your daily totals..."
];

const FINAL_RESPONSE = [
    "All set! I've updated your daily macro totals.",
    "Done! Those calories are officially in the books.",
    "Got it, your nutrition tracker has been updated.",
    "Success! I've added the meal to your daily logs.",
    "Macros crunched, ratios calculated, and totals updated."
]

const INITIAL_DELAY = 500;
const GENERATING_DELAY = 700;
const PHRASE_TRANSITION_DELAY = 300; 
const WELCOME_DELAY = 600;

/* Helper: Pauses for X milliseconds */
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

/* Helper: Scrolls to the bottom of the chat history */
const scrollToBottom = (elementId = "chat-history") => {
    const el = document.getElementById(elementId);
    if (el) el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
}

/* Helper: Returns current local datetime in yyyy-MM-ddTHH:mm format */
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

/* Helper: Escapes text before inserting into HTML */
function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

/* Appends a message to the chat history */
const appendMessage = (text, isUser = true) => {
    const chatHistory = document.getElementById("chat-history");

    if (!chatHistory) return;

    const wrapper = document.createElement('div');
    const bubble = document.createElement('div');
    const avatar = document.createElement('div');

    avatar.className = 'd-flex justify-content-center align-items-center flex-shrink-0 rounded-2 fw-bold';
    avatar.style.width = '40px';
    avatar.style.height = '40px';

    bubble.style.maxWidth = '85%';
    bubble.style.wordBreak = 'break-word';
    bubble.textContent = text;
    
    if (isUser) {
        wrapper.className = 'd-flex justify-content-end w-100 chat-bubble-wrapper mb-3 gap-3';
        bubble.className = 'bg-accent-1 text-black rounded-4 px-3 py-2 shadow-sm';
        wrapper.append(bubble, avatar);
    } else {
        wrapper.className = 'd-flex justify-content-start w-100 chat-bubble-wrapper mb-3 gap-3';
        bubble.className = 'text-theme-primary rounded-4 px-3 py-2 shadow-sm';
        bubble.style.backgroundColor = 'var(--bg-input)'; 
        avatar.textContent = 'A.';
        avatar.style.backgroundColor = 'var(--accent-1)';
        avatar.style.color = 'black';
        wrapper.append(avatar, bubble);
    }

    chatHistory.appendChild(wrapper);
    scrollToBottom();
    return { bubble, avatar }; 
};

const toggleChatLock = (isLocked) => {
    const input = document.getElementById("logging-input");
    const sendBtn = document.getElementById("send-meal-btn");
    if (!input || !sendBtn) return;

    input.disabled = isLocked;

    if (isLocked) {
        sendBtn.classList.remove('bg-white', 'text-bg-primary');
        sendBtn.classList.add('bg-secondary', 'text-white-50');
        sendBtn.style.pointerEvents = 'none';
    } else {
        sendBtn.classList.remove('bg-secondary', 'text-white-50');
        sendBtn.classList.add('bg-white', 'text-bg-primary');
        sendBtn.style.pointerEvents = 'auto';
    }
};

const playAnimationAndSubmit = async (query) => {
    const { bubble, avatar } = appendMessage("", false);
    avatar.classList.add('avatar-breathing');
    bubble.className = 'text-secondary px-3 py-2 fst-italic fade-text';
    bubble.style.backgroundColor = 'transparent';
    bubble.style.opacity = 0;

    await delay(INITIAL_DELAY);
    
    // Play the generating animation
    for (const phrase of GENERATING_PHRASES) {
        bubble.textContent = phrase;
        bubble.style.opacity = 1;

        scrollToBottom();

        await delay(GENERATING_DELAY);
        bubble.style.opacity = 0;
        await delay(PHRASE_TRANSITION_DELAY);
    }

    bubble.textContent = ""; 
    bubble.style.opacity = 0;

    try {
        // Fetch food data from the API
        const response = await fetch("/calorieTracker/search", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                "Accept": "application/json",
                "X-Requested-With": "XMLHttpRequest",
            },
            body: new URLSearchParams({ foodDescription: query }),
        });

        if (!response.ok) throw new Error("Search failed");
        const foods = await response.json();

        // Handle Empty Results
        if (!foods || foods.length === 0) {
            bubble.className = 'text-theme-primary rounded-4 px-3 py-3 shadow-sm';
            bubble.style.backgroundColor = 'var(--bg-input)';
            bubble.innerHTML = `<span class="text-danger fw-bold">No foods found.</span> Please try being more specific!`;
            toggleChatLock(false);
            return;
        }

        // Build the in-chat form
        bubble.style.opacity = 0;
        await delay(PHRASE_TRANSITION_DELAY);

        let dynamicFoodsHtml = "";
        let foodNames = [];

        foods.forEach(food => {
            const foodName = escapeHtml(food.foodName ?? food.name ?? "Unknown food");
            foodNames.push(foodName);
            
            // Restore actual serving size from API
            const servSize = Number(food.servSize ?? food.serving_size_g ?? 100); 

            dynamicFoodsHtml += `
                <div class="d-flex align-items-center gap-2 mb-2">
                    <div class="d-flex align-items-center rounded-3 px-3 py-2 w-100" style="background-color: var(--bg-subtle-box);">
                        <input type="text" class="bg-transparent border-0 text-theme-primary w-100" value="${foodName}" readonly style="outline: none;">
                        
                        <input type="hidden" name="foodOrder" value="${foodName}" />
                    </div>
                    <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: var(--bg-subtle-box); width: 85px; flex-shrink: 0;">
                        
                        <input type="number" step="0.1" min="0.1" name="requestedServSizes[${foodName}]" class="bg-transparent border-0 text-theme-primary w-100 text-center" value="${servSize}" style="outline: none;">
                        <span class="text-secondary ms-1">g</span>
                    </div>
                </div>
            `;
        });

        // Inject the full form into the bubble
        bubble.innerHTML = `
            <form id="chat-save-form" class="d-flex flex-column gap-3" style="font-size: 0.9rem;">
                <input type="hidden" name="consumedDate" value="${getCurrentDateTimeLocal()}">

                <p class="mb-0 text-theme-primary fw-bold">Review Your Meal</p>
                
                <div>
                    <label class="form-label text-secondary mb-1" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Meal Title</label>
                    <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: var(--bg-subtle-box);">
                        <input type="text" name="mealName" class="bg-transparent border-0 text-theme-primary w-100" value="${foodNames.join(', ')}" style="outline: none;" required>
                    </div>
                </div>

                <div>
                    <label class="form-label text-secondary mb-1" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Meal Type</label>
                    <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: var(--bg-subtle-box);">
                        <select name="mealType" class="bg-transparent border-0 text-theme-primary w-100" style="outline: none; -webkit-appearance: none; appearance: none; cursor: pointer;" required>
                            <option value="Breakfast" class="text-black">Breakfast</option>
                            <option value="Lunch" class="text-black">Lunch</option>
                            <option value="Dinner" class="text-black">Dinner</option>
                            <option value="Snack" class="text-black" selected>Snack</option>
                        </select>
                    </div>
                </div>

                <div>
                    <label class="form-label text-secondary mb-1" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Detected Foods</label>
                    ${dynamicFoodsHtml}
                </div>

                <button type="submit" class="btn btn-sm w-100 fw-bold mt-2" style="background-color: var(--accent-1); color: #172018; border-radius: 12px; border: none; padding: 0.6rem;">Confirm & Save</button>
            </form>
        `;
        
        bubble.className = 'text-white rounded-4 px-3 py-3 shadow-sm fade-text';
        bubble.style.backgroundColor = 'var(--bg-input)';
        bubble.style.opacity = 1;
        scrollToBottom();

        // Listener for the form submission
        const chatForm = document.getElementById('chat-save-form');
        if (chatForm) {
            chatForm.addEventListener('submit', async (e) => {
                e.preventDefault(); // Stop default HTML submission behavior
                
                const submitBtn = chatForm.querySelector('button[type="submit"]');
                submitBtn.textContent = "Saving...";
                submitBtn.style.opacity = "0.7";
                submitBtn.disabled = true;

                try {
                    // Post the data to teammate's backend
                    const saveResponse = await fetch("/meals/add", {
                        method: 'POST',
                        body: new URLSearchParams(new FormData(chatForm)),
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    });

                    if (!saveResponse.ok) throw new Error("Save failed");
                    
                    avatar.classList.remove('avatar-breathing');
                    bubble.style.opacity = 0;

                    await delay(PHRASE_TRANSITION_DELAY);

                    const randomResponse = FINAL_RESPONSE[Math.floor(Math.random() * FINAL_RESPONSE.length)];

                    bubble.textContent = randomResponse;
                    bubble.className = 'text-theme-primary rounded-4 px-3 py-2 shadow-sm fade-text';
                    bubble.style.backgroundColor = 'var(--bg-input)';
                    bubble.style.opacity = 1;

                    scrollToBottom();

                    // Save the chat history and the UI state before refreshing
                    const historyContainer = document.getElementById("chat-history");
                    if (historyContainer) {
                        sessionStorage.setItem("savedChatHistory", historyContainer.innerHTML);
                        sessionStorage.setItem("isChatMode", "true");
                    }
                    
                    window.location.reload();

                } catch (err) {
                    console.error("Save error:", err);
                    submitBtn.textContent = "Error Saving";
                    submitBtn.style.backgroundColor = "#dc3545"; 
                    submitBtn.style.color = "white";
                }
            });
        }

    } catch (error) {
        console.error(error);
        bubble.className = 'text-theme-primary rounded-4 px-3 py-3 shadow-sm';
        bubble.style.backgroundColor = 'var(--bg-input)';
        bubble.innerHTML = `<span class="text-danger fw-bold">Server Error.</span> Could not process your request.`;
    }

    toggleChatLock(false);
};

const handleMessageSend = async (e) => {
    if (e) e.preventDefault();
    const container = document.getElementById("logging-container");
    const input = document.getElementById("logging-input");
    
    if (!input || input.value.trim() === "") return;

    const messageText = input.value.trim();
    
    toggleChatLock(true);

    if (container && !container.classList.contains("chat-mode")) {
        const welcome = document.getElementById("welcome-section");
        container.classList.add("chat-mode");
        if (welcome) {
            welcome.addEventListener("transitionend", () => welcome.style.display = "none", { once: true });
        }
        await delay(WELCOME_DELAY);
    } 
    
    appendMessage(messageText, true);
    input.value = ""; // Clear input immediately
    
    await playAnimationAndSubmit(messageText);
};

/* Initialize */
document.addEventListener("DOMContentLoaded", () => {
    
    // Restore Chat History if it exists
    // - Note: Temporary, history is wiped when tab is closed
    const savedChat = sessionStorage.getItem("savedChatHistory");
    const isChatMode = sessionStorage.getItem("isChatMode");
    const container = document.getElementById("logging-container");
    const chatHistory = document.getElementById("chat-history");

    if (savedChat && chatHistory) {
        // Paste the old chat bubbles back in
        chatHistory.innerHTML = savedChat;
        
        // Hide the welcome section and trigger chat mode
        if (isChatMode === "true" && container) {
            container.classList.add("chat-mode");
            const welcome = document.getElementById("welcome-section");
            if (welcome) welcome.style.display = "none";
        }

        const oldButtons = chatHistory.querySelectorAll('button[type="submit"]');
        oldButtons.forEach(btn => {
            btn.disabled = true;
            btn.textContent = "Saved";
            btn.style.opacity = "0.5";
        });

        scrollToBottom();
    }

    // Progress Circles Init
    document.querySelectorAll(".progress-circle").forEach(circle => {
        const value = circle.style.getPropertyValue("--value").trim();
        const text = circle.querySelector(".progress-value");
        if (text) text.textContent = `${value}%`;
    });

    // Event Listeners
    const sendBtn = document.getElementById("send-meal-btn");
    if (sendBtn) {
        sendBtn.addEventListener("click", handleMessageSend);
    }

    const loggingInput = document.getElementById("logging-input");
    if (loggingInput) {
        loggingInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter" && !e.shiftKey) {
                handleMessageSend(e);
            }
        });
    }
});