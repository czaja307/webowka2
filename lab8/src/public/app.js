const socket = io();

// DOM Elements
const joinContainer = document.getElementById('joinContainer');
const chatContainer = document.getElementById('chatContainer');
const usernameInput = document.getElementById('username');
const joinBtn = document.getElementById('joinBtn');
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');
const messagesDiv = document.getElementById('messages');
const typingIndicator = document.getElementById('typingIndicator');
const roomSelect = document.getElementById('roomSelect');
const imageBtn = document.getElementById('imageBtn');
const imageInput = document.getElementById('imageInput');

let username = '';
let typingTimeout;

// Join Chat Handler
function joinChat() {
    username = usernameInput.value.trim();
    if (username) {
        socket.emit('join', username);
        joinContainer.style.display = 'none';
        chatContainer.style.display = 'flex';
    }
}

joinBtn.addEventListener('click', joinChat);
usernameInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        joinChat();
    }
});

// Send Message Handler
sendBtn.addEventListener('click', sendMessage);
messageInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        sendMessage();
    }
});

function sendMessage() {
    const message = messageInput.value.trim();
    if (message) {
        socket.emit('chatMessage', { text: message });
        messageInput.value = '';
    }
}

// Typing Indicator Handler
messageInput.addEventListener('input', () => {
    socket.emit('typing', true);
    clearTimeout(typingTimeout);
    typingTimeout = setTimeout(() => {
        socket.emit('typing', false);
    }, 1000);
});

// Room Change Handler
roomSelect.addEventListener('change', () => {
    const newRoom = roomSelect.value;
    socket.emit('joinRoom', newRoom);
    messagesDiv.innerHTML = ''; // Clear messages when changing rooms
});

// Image Upload Handler
imageBtn.addEventListener('click', () => imageInput.click());
imageInput.addEventListener('change', handleImageUpload);

function handleImageUpload(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            socket.emit('imageMessage', { image: e.target.result });
        };
        reader.readAsDataURL(file);
    }
}

// Socket Event Listeners
socket.on('message', (data) => {
    addMessage(data);
});

socket.on('imageMessage', (data) => {
    addImageMessage(data);
});

socket.on('userJoined', (data) => {
    addSystemMessage(data.message);
});

socket.on('userLeft', (data) => {
    addSystemMessage(data.message);
});

socket.on('userTyping', (data) => {
    if (data.isTyping) {
        typingIndicator.textContent = `${data.username} is typing...`;
    } else {
        typingIndicator.textContent = '';
    }
});

socket.on('user joined', (username) => {
    appendMessage(`${username} joined the chat`);
});

socket.on('user left', (username) => {
    appendMessage(`${username} left the chat`);
});

socket.on('chat message', (data) => {
    appendMessage(`${data.username}: ${data.message}`);
});

socket.on('user typing', (username) => {
    typingIndicator.textContent = `${username} is typing...`;
});

socket.on('stop typing', () => {
    typingIndicator.textContent = '';
});

// Create modal element
const modal = document.createElement('div');
modal.className = 'modal';
document.body.appendChild(modal);

// Image click handler
function handleImageClick(event) {
    if (event.target.tagName === 'IMG' && !event.target.closest('.modal')) {
        const modalImg = document.createElement('img');
        modalImg.src = event.target.src;
        modal.innerHTML = '';
        modal.appendChild(modalImg);
        modal.classList.add('active');
    }
}

// Close modal when clicking on the background (not the image)
modal.addEventListener('click', (event) => {
    if (event.target === modal) {
        modal.classList.remove('active');
    }
});

// Close modal with Escape key
document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && modal.classList.contains('active')) {
        modal.classList.remove('active');
    }
});

// Add click listener to messages div for image clicks
messagesDiv.addEventListener('click', handleImageClick);

// Helper Functions
function addMessage(data) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${data.senderId === socket.id ? 'sent' : 'received'}`;
    
    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';
    bubble.textContent = data.text;
    
    const info = document.createElement('div');
    info.className = 'message-info';
    info.textContent = `${data.username} • ${new Date(data.timestamp).toLocaleTimeString()}`;
    
    messageDiv.appendChild(bubble);
    messageDiv.appendChild(info);
    messagesDiv.appendChild(messageDiv);
    scrollToBottom();
}

function addImageMessage(data) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${data.senderId === socket.id ? 'sent' : 'received'}`;
    
    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';
    
    const img = document.createElement('img');
    img.src = data.image;
    bubble.appendChild(img);
    
    const info = document.createElement('div');
    info.className = 'message-info';
    info.textContent = `${data.username} • ${new Date(data.timestamp).toLocaleTimeString()}`;
    
    messageDiv.appendChild(bubble);
    messageDiv.appendChild(info);
    messagesDiv.appendChild(messageDiv);
    scrollToBottom();
}

function addSystemMessage(message) {
    const messageDiv = document.createElement('div');
    messageDiv.className = 'system-message';
    messageDiv.textContent = message;
    messagesDiv.appendChild(messageDiv);
    scrollToBottom();
}

function scrollToBottom() {
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}