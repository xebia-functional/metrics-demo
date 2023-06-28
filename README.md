## Metrics demo

This is a sample repository for generating metrics from different frameworks

### Running

1. Use docker compose file in the `services` folder for starting the dependent services (Prometheus and Grafana):

```bash
docker compose up -d
```

2. Run the application

```bash
cd http4s-metrics-sample
sbt run
```

3. Using a bundle distribution of Gatling (https://gatling.io/open-source/) run the simulation

```bash
cd <gatling-root>
bin/gatling.sh -rm local -sf <project-root>/gatling-simulations -rd "Gatling local metrics" -erjo "-Dusers=10 -Dminutes=5"
```