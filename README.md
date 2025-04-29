# WebCrawler Project 🕷️

**Information Retrieval Course Assignment**  
*Faculty of Computers and Artificial Intelligence, Cairo University*  
*Spring 2025*

A Java-based web crawler with TF-IDF ranking and cosine similarity implementation for document retrieval.

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://java.com)
[![Jsoup](https://img.shields.io/badge/Jsoup-1.17.2-green)](https://jsoup.org)

## Features ✨
- **Wikipedia-focused crawler** with BFS traversal
- **10-page limit** with duplicate prevention
- Inverted index construction with TF-IDF weighting
- Query processing using cosine similarity
- Top-10 document ranking system

## Installation ⚙️

### Prerequisites
- Java 17+
- Maven 3.6+
- Jsoup 1.17.2

```bash
git clone https://github.com/your-username/WebCrawler.git
```

### IDE Setup (IntelliJ)
1. Import Project: File > New > Project from Existing Sources
2. Maven Configuration: Select `pom.xml`
3. Add Jsoup Dependency:
```xml
<dependency>
<groupId>org.jsoup</groupId>
<artifactId>jsoup</artifactId>
<version>1.17.2</version>
</dependency>
```
4. Build project: `mvn clean install`

## Implementation Details 🔍
- **Web Crawler**: BFS with URL normalization
- **Text Processing**: Tokenization and case normalization
- **Indexing**: HashMap-based inverted index
- **Ranking**: Vector space model implementation
