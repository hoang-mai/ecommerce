db = db.getSiblingDB("read-service");

db.createUser({
    user: "read-service",
    pwd: "read-service",
    roles: [
        { role: "dbOwner", db: "read-service" }
    ]
});

db = db.getSiblingDB("notification-service");

db.createUser({
    user: "notification-service",
    pwd: "notification-service",
    roles: [
        { role: "dbOwner", db: "notification-service" }
    ]
});

db = db.getSiblingDB("chat-service");

db.createUser({
    user: "chat-service",
    pwd: "chat-service",
    roles: [
        { role: "dbOwner", db: "chat-service" }
    ]
});