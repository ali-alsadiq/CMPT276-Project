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

    // Show random final response
    const randomResponse = FINAL_RESPONSE[Math.floor(Math.random() * FINAL_RESPONSE.length)];
    bubble.textContent = randomResponse;
    bubble.className = 'text-white rounded-4 px-3 py-2 shadow-sm fade-text';
    bubble.style.backgroundColor = 'var(--bg-input)';
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