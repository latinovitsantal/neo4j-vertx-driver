# Neo4j Vert.x Driver

This repository makes reusing Vert.x event loop for Neo4J driver possible.
It achieves it's goal by forking the original Neo4J Java Driver repositor, unshading Netty dependencies and providing a function that takes a verticle as argument.
