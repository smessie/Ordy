# Ordy Spring Backend

## Running the backend

### Before you start

- Setup `mariadb` server, this is the dbms we will use for this project.
    - Create an empty database
    - Create a user for the new database, for example I used `ordy-dev`. A localhost user should be enough.
    
- Create a `.env` file in the project root, this should be based upon `.env.template`. Edit the file where needed.
- If you are on windows create a `.env.bat` file in the project root, this should be based upon `.env.template.bat`. Edit the file where needed.

### Start the backend

To start the server run:
- Linux: `./runapp.sh spring-boot:run`
- windows: `runapp.cmd spring-boot:run`

There is swagger documentation available at `/swagger-ui.html`, this has some issues with running on windows.