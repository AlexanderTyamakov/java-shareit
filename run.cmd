docker-compose up -d

docker exec -it shareit-server /bin/bash -c "ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && dpkg-reconfigure -f noninteractive tzdata"
docker exec -it shareit-gateway /bin/bash -c "ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && dpkg-reconfigure -f noninteractive tzdata"
docker exec -it shareit-db /bin/bash -c "ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime && dpkg-reconfigure -f noninteractive tzdata"
docker exec -it shareit-server /bin/bash -c "chmod 777 /var/log/shareit/server"
docker exec -it shareit-gateway /bin/bash -c "chmod 777 /var/log/shareit/gateway"
docker exec -it shareit-db /bin/bash -c "chmod 777 /var/log/postgres"