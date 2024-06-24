<h1 style="display: flex; justify-content: center">💰 Pocketful</h1>
<p style="display: flex; justify-content: center">A Rest Java API for expenses management</p>

### 🔧 Tooling

- [Docker](https://docs.docker.com/desktop/)

### ⚙️  Running

- Create a .env file using the example below:

```
DATABASE_NAME=pocketful
DATABASE_URL=jdbc:postgresql://db:5432/pocketful
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

MAIL_HOST=sandbox.smtp.host.io
MAIL_USERNAME=username
MAIL_PASSWORD=password
MAIL_PROTOCOL=smtp
MAIL_PORT=2525
```

- Build project using Docker:

```
docker compose up
```