<h1 style="display: flex; justify-content: center">💰 Pocketful</h1>
<p style="display: flex; justify-content: center">A Rest Java API for expenses management</p>

### 🔧 Tooling

- [Docker](https://docs.docker.com/desktop/)

### ⚙️  Running

- Create a .env file using the example below:

```
DATABASE_NAME=
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

MAIL_HOST=
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_PROTOCOL=
MAIL_PORT=

JWT_SECRET=

PAYMENTS_GENERATION_QUEUE=
PAYMENTS_EDITION_QUEUE=
PENDING_PAYMENT_NOTIFICATION_TIME=

RABBITMQ_HOST=
RABBITMQ_PORT=
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
```

- Build project using Docker:

```
docker compose up
```