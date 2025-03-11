// WebDriver 감지 우회
Object.defineProperty(navigator, 'webdriver', {
    get: () => false,
    configurable: true,
    enumerable: true,
    writable: true
});

// 추가: Chrome 확장 프로그램 감지 우회
Object.defineProperty(navigator, 'plugins', {
    get: () => [1, 2, 3], // 더미 데이터
});

// UserAgent에서 Headless 키워드 제거
Object.defineProperty(navigator, 'userAgent', {
    get: () => "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
});
