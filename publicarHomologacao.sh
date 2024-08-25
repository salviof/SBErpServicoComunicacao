
BRANCH_ATUAL="master"
NOME_GRUPO_PROJETO="agenciaForms"
cd /home/superBits/projetos/Casa_Nova/source/$NOME_GRUPO_PROJETO/modelRegras
mvn -DskipTests=true clean install
cd /home/superBits/projetos/Casa_Nova/source/$NOME_GRUPO_PROJETO/ws 
mvn -DskipTests=true clean install
mkdir /home/superBits/projetos/Casa_Nova/release/agenciaForms/ws/deploy/master -p
cp /home/superBits/projetos/Casa_Nova/source/$NOME_GRUPO_PROJETO/ws/target/ws-1.0-SNAPSHOT.one-jar.jar /home/superBits/projetos/Casa_Nova/release/agenciaForms/ws/deploy/master
cp /home/superBits/projetos/Casa_Nova/source/agenciaForms/ws/src/main/resources/chatClient/* /home/superBits/projetos/Casa_Nova/release/agenciaForms/ws/deploy/master -R

cp /home/superBits/projetos/Casa_Nova/source/$NOME_GRUPO_PROJETO/ws/src/main/resources/deploy/Dockerfile /home/superBits/projetos/Casa_Nova/release/agenciaForms/ws/deploy/master
cd /home/superBits/projetos/Casa_Nova/release/$NOME_GRUPO_PROJETO/ws/deploy/master

sudo docker build -t casanovadigital:contatosws .
DIRETORIO_DESTINO_REMOTO=/opt/traefik/configServidor/jenkins/workspace/ws/$NOME_GRUPO_PROJETO/deploy/$BRANCH_ATUAL
rsync -avzh --exclude='*/.git'  -e "ssh -p 667" /home/superBits/projetos/Casa_Nova/release/agenciaForms/ws/deploy/master/*   root@casanovadigital.com.br:$DIRETORIO_DESTINO_REMOTO
