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