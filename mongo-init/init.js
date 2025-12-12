db = db.getSiblingDB("read-service");

db.createUser({
    user: "read-service",
    pwd: "read-service",
    roles: [
        { role: "dbOwner", db: "read-service" }
    ]
});

db = db.getSiblingDB("chat-notification-service");

db.createUser({
    user: "chat-notification-service",
    pwd: "chat-notification-service",
    roles: [
        { role: "dbOwner", db: "chat-notification-service" }
    ]
});