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
const FINAL_DELAY = 200;             // * 5 for random delay
const PHRASE_TRANSITION_DELAY = 300; // Do not change
const FINAL_TRANSITION_DELAY = 50;   // Do not change
const WELCOME_DELAY = 600;

/* Helper: Pauses for X milliseconds */
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

/* Helper: Gets a random integer from 1-5 */
function getRandom() { return Math.floor(Math.random() * 5) + 1; }

/* Helper: Event listeners without crashing if the button is missing */
const addSafeListener = (id, event, callback) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener(event, callback);
};

/* Helper: Scrolls to the bottom of the chat history */
const scrollToBottom = (elementId = "chat-history") => {
    const el = document.getElementById(elementId);
    if (el) el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
}

/**
 * Generates the response sequence for the chatbot.
 * 
 * 1. Appends an empty response bubble
 * 2. Cycles through generating phrases
 * 3. TODO: Food Quantity Editor <--
 * 4. Displays final response
 */
const generateResponse = async () => {
    const { bubble, avatar } = appendMessage("", false);

    avatar.classList.add('avatar-breathing');

    bubble.className = 'text-secondary px-3 py-2 fst-italic fade-text';
    bubble.style.backgroundColor = 'transparent';

    bubble.style.opacity = 0;

    await delay(INITIAL_DELAY);

    for (const phrase of GENERATING_PHRASES) {
        bubble.textContent = phrase;
        bubble.style.opacity = 1;
        scrollToBottom();

        await delay(GENERATING_DELAY);

        bubble.style.opacity = 0;

        await delay(PHRASE_TRANSITION_DELAY);
    }

    await delay(FINAL_DELAY * getRandom());

    // Final state
    avatar.classList.remove('avatar-breathing');
    bubble.className = 'text-white rounded-4 px-3 py-2 shadow-sm fade-text';
    bubble.style.backgroundColor = 'var(--bg-input)';
    bubble.textContent = FINAL_RESPONSE[getRandom() - 1];
    
    await delay(FINAL_TRANSITION_DELAY);

    bubble.style.opacity = 1;

    scrollToBottom();
}

/**
 * Locks the send button, but leaves the text box open
 * 
 * @param {boolean} isLocked - Lock (true) or unlock (false)
 */
const toggleChatLock = (isLocked) => {
    const input = document.getElementById("logging-input");
    const sendBtn = document.getElementById("send-meal-btn");

    if (!input || !sendBtn) return;

    input.dataset.locked = isLocked;

    if (isLocked) {
        sendBtn.classList.remove('bg-white', 'text-bg-primary');
        sendBtn.classList.add('bg-secondary', 'text-white-50');
        sendBtn.style.pointerEvents = 'none';
    } 
    
    else {
        sendBtn.classList.remove('bg-secondary', 'text-white-50');
        sendBtn.classList.add('bg-white', 'text-bg-primary');
        sendBtn.style.pointerEvents = 'auto';
    }
};

/**
 * Appends a message to the chat history
 * 
 * @param {string} text - What appears in the text bubble
 * @param {boolean} isUser - user message (true), bot message (false)
 */
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
    } 

    else {
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

/* Handles user message input and initial message animation */
const handleMessageSend = async () => {
    const container = document.getElementById("logging-container");
    const input = document.getElementById("logging-input");
    
    if (!input || input.value.trim() === "") return;

    const messageText = input.value.trim();
    input.value = ""; 

    // Messages blocked during this sequence
    toggleChatLock(true);
    {
        // Animation for first message
        if (!container.classList.contains("chat-mode")) {
            const welcome = document.getElementById("welcome-section");
            
            container.classList.add("chat-mode"); // Trigger CSS animations

            if (welcome) {
                welcome.addEventListener("transitionend", () => welcome.style.display = "none", { once: true });
            }
            await delay(WELCOME_DELAY);
        } 
        
        appendMessage(messageText, true);
        await generateResponse();
    }
    toggleChatLock(false);
};

/* Initialize */
document.addEventListener("DOMContentLoaded", () => {
    
    // Progress Circles
    document.querySelectorAll(".progress-circle").forEach(circle => {
        const value = circle.style.getPropertyValue("--value").trim();
        const text = circle.querySelector(".progress-value");
        if (text) text.textContent = `${value}%`;
    });

    // Event Listeners
    addSafeListener("send-meal-btn", "click", handleMessageSend);

    const loggingInput = document.getElementById("logging-input");
    if (loggingInput) {
        loggingInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault(); 
                if (loggingInput.dataset.locked !== "true") {
                    handleMessageSend();
                }
            }
        });
    }
});