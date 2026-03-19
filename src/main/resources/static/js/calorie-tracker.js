// TODO:
// - Entire page should scroll down, currently the entire page (incl the bottom) moves down
// - Add profile picutres to messages
// - Move response sequence to a helper function
// - Implement food serving display cards
// - Implement actual API returns

// --- UTILITIES & HELPERS ---

// Helper: Safely attach event listeners without crashing if the button is missing
const addSafeListener = (id, event, callback) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener(event, callback);
};

// Helper: A modern "sleep" function. Tells JavaScript to pause for X milliseconds.
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));


// --- UI INITIALIZATION ---

// Progress Circle Animaton
document.querySelectorAll(".progress-circle").forEach(circle => {
    const value = circle.style.getPropertyValue("--value").trim();
    const text = circle.querySelector(".progress-value");

    if (text) {
        text.textContent = `${value}%`;
    }
});

// Handles chat messages
const appendMessage = (text, sender = 'user') => {
    const chatHistory = document.getElementById("chat-history");
    if (!chatHistory) return;

    const messageWrapper = document.createElement('div');
    const messageBubble = document.createElement('div');
    
    // Style differently based on who is talking
    if (sender === 'user') {
        messageWrapper.className = 'd-flex justify-content-end w-100 chat-bubble-wrapper mb-3';
        messageBubble.className = 'bg-accent-1 text-black rounded-4 px-3 py-2 shadow-sm';
    } 
    
    else if (sender == 'response') {
        messageWrapper.className = 'd-flex justify-content-start w-100 chat-bubble-wrapper mb-3';
        messageBubble.className = 'text-white rounded-4 px-3 py-2 shadow-sm';
        messageBubble.style.backgroundColor = 'var(--bg-input)'; 
    }

    messageBubble.style.maxWidth = '85%';
    messageBubble.style.wordBreak = 'break-word';
    messageBubble.textContent = text;

    // Attach to page and auto-scroll
    messageWrapper.appendChild(messageBubble);
    chatHistory.appendChild(messageWrapper);
    
    chatHistory.scrollTo({
        top: chatHistory.scrollHeight,
        behavior: 'smooth'
    });
};


// --- CORE CHATBOX AND WELCOME LOGIC ---

// Handles user message input
const handleMessageSend = async () => {
    const container = document.getElementById("logging-container");
    const input = document.getElementById("logging-input");
    
    if (!input || input.value.trim() === "") {
        return;
    }

    // Capture text and immediately clear the input box
    const messageText = input.value.trim();
    input.value = ""; 

    const isFirstMessage = !container.classList.contains("chat-mode");

    // Animation for first message
    if (isFirstMessage) {
        const welcome = document.getElementById("welcome-section");
        
        container.classList.add("chat-mode"); // Trigger CSS animations

        if (welcome) {
            welcome.addEventListener("transitionend", () => {
                welcome.style.display = "none";
            }, { once: true });
        }

        await delay(600);
        appendMessage(messageText, 'user');
        
        // Inital message response
        await delay(800);
        appendMessage("Got it! I'm analyzing those macros for you now...", 'response');

    } 
    
    // Subsequent messages
    else {
        appendMessage(messageText, 'user');
        
        // Subsequent message response
        await delay(800);
        appendMessage("I've added that to your daily total.", 'response');
    }
};


// --- EVENT LISTENERS ---

addSafeListener("send-meal-btn", "click", handleMessageSend);

const loggingInput = document.getElementById("logging-input");
if (loggingInput) {
    loggingInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault(); 
            handleMessageSend();
        }
    });
}