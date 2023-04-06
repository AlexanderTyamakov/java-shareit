docker-compose up -d

docker exec -it -v /path/to/log/directory:/var/log/shareit/server shareit-server /bin/bash
docker exec -it -v /path/to/log/directory:/var/log/shareit/gateway shareit-gateway /bin/bash
docker exec -it -v /path/to/log/directory:/var/log/shareit/db postgres:13.7-alpine /bin/bash

docker exec -it shareit-server /bin/bash -c "ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && dpkg-reconfigure -f noninteractive tzdata"
docker exec -it shareit-server /bin/bash -c "chmod 777 /var/log/shareit/server"
docker exec -it shareit-gateway /bin/bash -c "ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && dpkg-reconfigure -f noninteractive tzdata"
docker exec -it shareit-gateway /bin/bash -c "chmod 777 /var/log/shareit/gateway"