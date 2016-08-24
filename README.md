GenerationGap Compile (Gapile) Test
=======================
test project for generation-gap compile for compile speed

to suppress compile cost for application developers,  
so want to use jar reference to generated base-classes

# Structure
- gapile-base: has gapile-dfgenerated.jar in repo as maven repository
- gapile-common: has DBFlute client, extended-classes without base-classes, refers jar
- gapile-dfgenerated: has base-classes with dummy extended-classes, becomes jar

```
gapile-common
 |-src/main/java
 |  |-org.docksidestage.dbflute // developers can customize them
 |     |-cbean    // e.g. MemberCB, MemberCQ
 |     |-exbhv    // e.g. MemberBhv
 |     |-exentity // e.g. Member
 |
 |-dbflute_gapiledb
 |-mydbflute
 |-...

gapile-dfgenerated
 |-src/main/java
 |  |-org.docksidestage.dbflute // developers can customize them
 |     |-allcommon  // e.g. BsMemberCB, BsMemberCQ
 |     |-bsbhv      // e.g. BsMemberBhv
 |     |-bsentity   // e.g. BsMember, MemberDbm
 |     |-cbean      // dummy e.g. MemberCB, MemberCQ
 |     |-exbhv      // dummy e.g. MemberBhv
 |     |-exentity   // dummy e.g. Member
 |
 |-...
```

# DBFlute Settings
TODO jflute make gapile option

# Information
## License
Apache License 2.0

## Official site
coming soon...
