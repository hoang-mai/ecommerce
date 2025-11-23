db = db.getSiblingDB("read-service");

db.createUser({
    user: "read-service",
    pwd: "read-service",
    roles: [
        { role: "dbOwner", db: "read-service" }
    ]
});
