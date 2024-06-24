<h1 style="display: flex; justify-content: center">💰 Pocketful</h1>
<p style="display: flex; justify-content: center">A Rest Java API for expenses management</p>

### 🔧 Tooling

- [Docker](https://docs.docker.com/desktop/)

### ⚙️  Running

- Create a .env file using the example below:

```
DATABASE_NAME=pocketful
DATABASE_URL=jdbc:postgresql://db:5432/pocketful
DATABASE_USERNAME=
DATABASE_PASSWORD=

MAIL_HOST=
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_PROTOCOL=
MAIL_PORT=
```

- Build project using Docker:

```
docker compose up
```