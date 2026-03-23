/* Constants */
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
        bubble.className = 'text-white rounded-4 px-3 py-2 shadow-sm';
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

/* Plays the generation animation and then submits the form for a real page refresh. */
const playAnimationAndSubmit = async () => {
    const { bubble, avatar } = appendMessage("", false);
    avatar.classList.add('avatar-breathing');
    bubble.className = 'text-secondary px-3 py-2 fst-italic fade-text';
    bubble.style.backgroundColor = 'transparent';
    bubble.style.opacity = 0;

    await delay(INITIAL_DELAY);
    
    // Animate phrases
    for (const phrase of GENERATING_PHRASES) {
        bubble.textContent = phrase;
        bubble.style.opacity = 1;
        scrollToBottom();
        await delay(GENERATING_DELAY);
        bubble.style.opacity = 0;
        await delay(PHRASE_TRANSITION_DELAY);
    }

    avatar.classList.remove('avatar-breathing');

    // Show mock form
    bubble.innerHTML = `
        <div class="d-flex flex-column gap-3" style="font-size: 0.9rem;">
            <p class="mb-0 text-white fw-bold">Review Your Meal</p>
            
            <!-- Title -->
            <div>
                <label class="form-label text-secondary mb-1" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Meal Title</label>
                <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: rgba(255,255,255,0.05);">
                    <input type="text" class="bg-transparent border-0 text-white w-100" value="Morning Scramble" style="outline: none;">
                </div>
            </div>

            <!-- Type -->
            <div>
                <label class="form-label text-secondary mb-1" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Meal Type</label>
                <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: rgba(255,255,255,0.05);">
                    <select class="bg-transparent border-0 text-white w-100" style="outline: none; -webkit-appearance: none; appearance: none; cursor: pointer;">
                        <option value="breakfast" selected class="text-black">Breakfast</option>
                        <option value="lunch" class="text-black">Lunch</option>
                        <option value="dinner" class="text-black">Dinner</option>
                        <option value="snack" class="text-black">Snack</option>
                    </select>
                </div>
            </div>

            <!-- Foods (No Sub Container) -->
            <div>
                <label class="form-label text-secondary mb-1" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Detected Foods</label>
                
                <div class="d-flex align-items-center gap-2 mb-2">
                    <div class="d-flex align-items-center rounded-3 px-3 py-2 w-100" style="background-color: rgba(255,255,255,0.05);">
                        <input type="text" class="bg-transparent border-0 text-white w-100" value="Scrambled Eggs" style="outline: none;">
                    </div>
                    <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: rgba(255,255,255,0.05); width: 85px; flex-shrink: 0;">
                        <input type="number" class="bg-transparent border-0 text-white w-100 text-center" value="150" style="outline: none;">
                        <span class="text-secondary ms-1">g</span>
                    </div>
                </div>

                <div class="d-flex align-items-center gap-2">
                    <div class="d-flex align-items-center rounded-3 px-3 py-2 w-100" style="background-color: rgba(255,255,255,0.05);">
                        <input type="text" class="bg-transparent border-0 text-white w-100" value="Whole Wheat Toast" style="outline: none;">
                    </div>
                    <div class="d-flex align-items-center rounded-3 px-3 py-2" style="background-color: rgba(255,255,255,0.05); width: 85px; flex-shrink: 0;">
                        <input type="number" class="bg-transparent border-0 text-white w-100 text-center" value="60" style="outline: none;">
                        <span class="text-secondary ms-1">g</span>
                    </div>
                </div>
            </div>

            <button id="mock-done-btn" class="btn btn-sm w-100 fw-bold mt-2" style="background-color: var(--accent-1); color: #172018; border-radius: 12px; border: none; padding: 0.6rem;">Done</button>
        </div>
    `;
    bubble.className = 'text-white rounded-4 px-3 py-3 shadow-sm fade-text';
    bubble.style.backgroundColor = 'var(--bg-input)';
    bubble.style.opacity = 1;
    scrollToBottom();

    // Wait for click
    await new Promise(resolve => {
        const btn = document.getElementById("mock-done-btn");
        if(btn) {
            btn.addEventListener("click", () => {
                // Collapse the form by fading it out
                bubble.style.opacity = 0;
                setTimeout(resolve, 300);
            });
        } else {
            resolve();
        }
    });

    // Show random final response
    const randomResponse = FINAL_RESPONSE[Math.floor(Math.random() * FINAL_RESPONSE.length)];
    bubble.innerHTML = ''; // Clear form
    bubble.textContent = randomResponse;
    bubble.className = 'text-white rounded-4 px-3 py-2 shadow-sm fade-text';
    bubble.style.opacity = 1;
    scrollToBottom();

    // Re-enable input so it's sent with the form
    const input = document.getElementById("logging-input");
    if (input) input.disabled = false;

    // Small delay before refresh
    await delay(1000);

    // Submit the form at the end
    document.getElementById("logging-form").submit();
};

/**
 * Locks the send button
 */
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

/* Handles the send button click or enter key */
const handleMessageSend = async (e) => {
    if (e) e.preventDefault();
    const container = document.getElementById("logging-container");
    const input = document.getElementById("logging-input");
    
    if (!input || input.value.trim() === "") return;

    const messageText = input.value.trim();
    
    // Messages blocked during this sequence
    toggleChatLock(true);

    if (!container.classList.contains("chat-mode")) {
        const welcome = document.getElementById("welcome-section");
        container.classList.add("chat-mode");
        if (welcome) {
            welcome.addEventListener("transitionend", () => welcome.style.display = "none", { once: true });
        }
        await delay(WELCOME_DELAY);
    } 
    
    appendMessage(messageText, true);
    await playAnimationAndSubmit();
};

/* Initialize */
document.addEventListener("DOMContentLoaded", () => {
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