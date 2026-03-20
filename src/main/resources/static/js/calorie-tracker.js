// TODO:
// - Refactor: readability in calorie-tracker.js, calorieTracker.css
// - Feat: Add profile picutres to messages
// - Move response sequence to a helper function
// - Implement food serving display cards
// - Feat: Implement actual API returns
// - Refactor: 


// --- HELPERS ---

// Helper: Event listeners without crashing if the button is missing
const addSafeListener = (id, event, callback) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener(event, callback);
};

// Helper: Pauses JavaScript for X milliseconds
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
    const avatar = document.createElement('div');

    avatar.className = 'd-flex justify-content-center align-items-center flex-shrink-0 rounded-2 fw-bold';
    avatar.style.width = '40px';
    avatar.style.height = '40px';

    messageBubble.style.maxWidth = '85%';
    messageBubble.style.wordBreak = 'break-word';
    messageBubble.textContent = text;
    messageBubble.classList.add('me-2');
    
    if (sender === 'user') {
        messageWrapper.className = 'd-flex justify-content-end w-100 chat-bubble-wrapper mb-3 gap-3';
        messageBubble.className = 'bg-accent-1 text-black rounded-4 px-3 py-2 shadow-sm';

        messageWrapper.appendChild(messageBubble);
        messageWrapper.appendChild(avatar);
    } 

    else {
        messageWrapper.className = 'd-flex justify-content-start w-100 chat-bubble-wrapper mb-3 gap-3';

        messageBubble.className = 'text-white rounded-4 px-3 py-2 shadow-sm';
        messageBubble.style.backgroundColor = 'var(--bg-input)'; 
        
        avatar.textContent = 'A.';
        avatar.style.backgroundColor = 'var(--accent-1)';
        avatar.style.color = 'var(--bg-primary)';

        messageWrapper.appendChild(avatar);
        messageWrapper.appendChild(messageBubble);
    }

    // Attach to page
    chatHistory.appendChild(messageWrapper);
    
    // Smooth scroll down to newest message
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


// --- EVENT LISTENER ---

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