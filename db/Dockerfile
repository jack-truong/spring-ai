FROM postgres:latest
COPY chinook_init.sql /docker-entrypoint-initdb.d/
EXPOSE 5432
CMD ["postgres"]
