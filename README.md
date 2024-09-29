# Secret Squirrel

## Development

```bash
docker volume create bastet-test-postgres && \
 docker rm -f bastet-test-postgres && \
 docker run --name bastet-test-postgres \
    -p 5432:5432 \
    -v bastet-test-postgres:/var/lib/postgresql/data \
    --restart unless-stopped \
    -e POSTGRES_PASSWORD=postgres -d postgres:16-bookworm
    # postgres@sha256:bc8a55fefb1461aaebd70047660d1f703f285bfd5b6bc39666a3263b7964e4c5 # 16-bookworm
```
