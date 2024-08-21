package com.cherisia.crawling;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class CrawlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlingApplication.class, args).getBean(CrawlingApplication.class).test();
    }

    public void test() {

        // 드라이버 설치 경로
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String DRIVER_PATH = "C:/Study/springboot/crawling/src/main/resources/static/chromedriver.exe";

        WebDriver driver = null;

        try {
            // WebDriver 경로 설정
            System.setProperty(WEB_DRIVER_ID, DRIVER_PATH);

            // WebDriver 옵션 생성, 창숨기기 설정
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

            driver = new ChromeDriver(chromeOptions);

            // 브라우저 작업수행, 페이지 이동
            driver.get("https://www.youtube.com/watch?v=BAeY4LPX79s");

            // 브라우저 정보요청
            String title = driver.getTitle();
            System.out.println("페이지 title : " + title);

            System.out.println("----페이지 전체 로딩 시작----");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            while (true) {
                // 현재 높이 저장
                Long beforeHeight = (Long) js.executeScript("return document.documentElement.scrollHeight");

                // 현재 높이만큼 스크롤
                js.executeScript("window.scrollTo(0, document.documentElement.scrollHeight)");

                // 새로운 내용이 로드될 때까지 대기
                Thread.sleep(2000);

                // 새로운 높이를 얻음
                Long afterHeight = (Long) js.executeScript("return document.documentElement.scrollHeight");

                // 새로운 높이가 이전 높이와 같으면 스크롤이 더는 내려가지 않은것으로 판단
                if (afterHeight.equals(beforeHeight)) {
                    break;
                }
            }

            System.out.println("----페이지 전체 로딩 완료----");

            // 작성자 아이디 author-text  / 내용 content-text / 좋아요 vote-count-middle
            List<WebElement> comments = driver.findElements(By.xpath("//*[@id=\"main\"]"));

            List<Map<String, Object>> commentList = new ArrayList<>();
            for (WebElement comment : comments) {
                Map<String, Object> commentMap = new HashMap<String, Object>();

                String author = comment.findElement(By.id("author-text")).getText();
                String content = comment.findElement(By.id("content-text")).getText();
                String vote = comment.findElement(By.id("vote-count-middle")).getText();

                commentMap.put("author", author);
                commentMap.put("content", content);
                commentMap.put("vote", vote);

                commentList.add(commentMap);
            }

            System.out.println("답글 제외한 댓글 개수 : " + commentList.size());

            for (Map<String, Object> item : commentList) {
                System.out.println("---------------------------");
                System.out.println("작성자 : " + item.get("author"));
                System.out.println("댓글 내용 : " + item.get("content"));
                System.out.println("좋아요 : " + item.get("vote"));
                System.out.println("---------------------------");
            }

            Random random = new Random();
            int randomIdx = random.nextInt(commentList.size());

            System.out.println("당첨된 댓글!!!");
            System.out.println("작성자 : " + commentList.get(randomIdx).get("author"));
            System.out.println("댓글 내용 : " + commentList.get(randomIdx).get("content"));
            System.out.println("좋아요 : " + commentList.get(randomIdx).get("vote"));


        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}
