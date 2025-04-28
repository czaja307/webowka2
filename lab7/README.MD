# GraphQL API Example Queries

## Queries

### Users

#### 1. Fetch All Users
```graphql
query {
  users {
    id
    name
    email
    login
    todos {
      id
      title
      completed
    }
  }
}
```

#### 2. Fetch User by ID
```graphql
query {
  user(id: 1) {
    id
    name
    email
    login
    todos {
      id
      title
      completed
    }
  }
}
```

### Todos

#### 3. Fetch All Todos
```graphql
query {
  todos {
    id
    title
    completed
    user {
      id
      name
      email
      login
    }
  }
}
```

#### 4. Fetch Todo by ID
```graphql
query {
  todo(id: 1) {
    id
    title
    completed
    user {
      id
      name
      email
      login
    }
  }
}
```

## Mutations

### Users

#### 1. Create a New User
```graphql
mutation {
  addUser(name: "John Doe", email: "john.doe@example.com", login: "johndoe") {
    id
    name
    email
    login
  }
}
```

#### 2. Update a User
```graphql
mutation {
  updateUser(id: 1, name: "John Updated", email: "john.updated@example.com", login: "johnupdated") {
    id
    name
    email
    login
  }
}
```

#### 3. Delete a User
```graphql
mutation {
    deleteUser(id: 1)
}
```

### Todos

#### 4. Create a New Todo
```graphql
mutation {
  addTodo(title: "Finish homework", completed: false, userId: 1) {
    id
    title
    completed
    user {
      id
      name
    }
  }
}
```

#### 5. Update a Todo
```graphql
mutation {
  updateTodo(id: 1, title: "Finish updated homework", completed: true, userId: 1) {
    id
    title
    completed
    user {
      id
      name
    }
  }
}
```

#### 6. Delete a Todo
```graphql
mutation {
  deleteTodo(id: 1)
}
```