
TODO:
  - [ ] Eureka Server logging `o.s.cloud.commons.util.InetUtils: Cannot determine local hostname` 
    - maybe an issue
  - [ ] need to change `auto.offset.reset` to `latest` and `auto.commit` to `true`
    - to prevent processing messages from the earliest
  - [ ] Add Product Service
    - consider using Redis since data on this service may be frequently retrieved
  - [ ] add here