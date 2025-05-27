mvn clean package -DskipTests

scp -r bot/target/bot-1.0.0.jar root@209.182.237.103:/data/deploy/bot/bot

scp -r trade/target/trade-1.0.0.jar root@209.182.237.103:/data/deploy/trade/trade

scp -r at-model/target/model-1.0.0.jar root@209.182.237.103:/data/deploy/trade/trade/lib

scp -r at-model/target/model-1.0.0.jar root@209.182.237.103:/data/deploy/bot/bot/lib