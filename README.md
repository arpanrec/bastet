# Secret Squirrel

## Development

```bash
docker volume create bastet-test-postgres && \
 docker network create bastet-test-postgres || true && \
 docker rm -f bastet-test-postgres && \
 docker rm -f bastet-test-pgadmin && \
 docker run --name bastet-test-postgres \
    -p 5432:5432 \
    -v bastet-test-postgres:/var/lib/postgresql/data \
    --restart no \
    --network bastet-test-postgres \
    -e POSTGRES_PASSWORD=postgres -d postgres:16-bookworm && \
 docker run --name bastet-test-pgadmin \
    -p 5050:80 \
    --restart no \
    --network bastet-test-postgres \
    -e 'PGADMIN_DEFAULT_PASSWORD=pgadmin' \
    -e 'PGADMIN_DEFAULT_EMAIL=admin@x.x' \
    -d dpage/pgadmin4
    # postgres@sha256:bc8a55fefb1461aaebd70047660d1f703f285bfd5b6bc39666a3263b7964e4c5 # 16-bookworm
```
