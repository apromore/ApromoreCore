# Uninstall old versions
sudo apt-get remove docker docker-engine docker.io containerd runc

#Uninstall the Docker Engine - Community package
sudo apt-get purge docker-ce

# Uninstall docker compose:
sudo rm -rf /usr/local/bin/docker-compose
sudo pip uninstall docker-compose


# SET UP THE REPOSITORY

sudo apt-get update

sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common


curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -


sudo apt-key fingerprint 0EBFCD88

sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"


# INSTALL DOCKER ENGINE - COMMUNITY
sudo apt-get update

sudo apt-get install docker-ce="5:19.03.2~3-0~ubuntu-bionic" docker-ce-cli="5:19.03.2~3-0~ubuntu-bionic" containerd.io 


# Install docker compose
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose



sudo docker run hello-world
# Hello from Docker!

docker-compose --version

