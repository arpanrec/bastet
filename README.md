# Secret Squirrel

## Development

```bash
docker volume create minerva-test-postgres && \
 docker rm -f minerva-test-postgres && \
 docker run --name minerva-test-postgres \
    -p 5432:5432 \
    -v minerva-test-postgres:/var/lib/postgresql/data \
    --restart no \
    -e POSTGRES_PASSWORD=postgres -d \
    postgres@sha256:bc8a55fefb1461aaebd70047660d1f703f285bfd5b6bc39666a3263b7964e4c5 # 16-bookworm
```
