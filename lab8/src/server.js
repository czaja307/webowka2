const express = require('express');
const app = express();
const http = require('http').createServer(app);
const io = require('socket.io')(http);
const path = require('path');

app.use(express.static(path.join(__dirname, 'public')));

// Store connected users
const users = new Map();
// Store typing status
const typingUsers = new Map();

io.on('connection', (socket) => {
    // Handle user joining
    socket.on('join', (username) => {
        users.set(socket.id, { username, room: 'main' });
        socket.join('main');
        io.to('main').emit('userJoined', {
            username: username,
            message: `${username} has joined the chat`
        });
    });

    // Handle chat messages
    socket.on('chatMessage', (data) => {
        const user = users.get(socket.id);
        if (user) {
            io.to(user.room).emit('message', {
                username: user.username,
                text: data.text,
                timestamp: new Date(),
                senderId: socket.id
            });
        }
    });

    // Handle typing status
    socket.on('typing', (isTyping) => {
        const user = users.get(socket.id);
        if (user) {
            socket.to(user.room).emit('userTyping', {
                username: user.username,
                isTyping: isTyping
            });
        }
    });

    // Handle image upload
    socket.on('imageMessage', (data) => {
        const user = users.get(socket.id);
        if (user) {
            io.to(user.room).emit('imageMessage', {
                username: user.username,
                image: data.image,
                timestamp: new Date(),
                senderId: socket.id
            });
        }
    });

    // Handle room change
    socket.on('joinRoom', (newRoom) => {
        const user = users.get(socket.id);
        if (user) {
            const oldRoom = user.room;
            socket.leave(oldRoom);
            socket.join(newRoom);
            user.room = newRoom;
            users.set(socket.id, user);
            
            io.to(oldRoom).emit('userLeft', {
                username: user.username,
                message: `${user.username} has left the room`
            });
            
            io.to(newRoom).emit('userJoined', {
                username: user.username,
                message: `${user.username} has joined the room`
            });
        }
    });

    // Handle disconnection
    socket.on('disconnect', () => {
        const user = users.get(socket.id);
        if (user) {
            io.to(user.room).emit('userLeft', {
                username: user.username,
                message: `${user.username} has left the chat`
            });
            users.delete(socket.id);
        }
    });
});

const PORT = process.env.PORT || 3000;
http.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});