function animateProgressCircle(circle) {
    const rawValue = parseFloat(circle.style.getPropertyValue("--value").trim());
    const targetValue = Number.isFinite(rawValue) ? Math.max(0, Math.min(rawValue, 100)) : 0;
    const duration = 1400;
    const startTime = performance.now();

    circle.style.setProperty("--animated-value", "0");

    function frame(now) {
        const elapsed = now - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        const currentValue = targetValue * eased;

        circle.style.setProperty("--animated-value", currentValue.toFixed(2));

        if (progress < 1) {
            requestAnimationFrame(frame);
        } else {
            circle.style.setProperty("--animated-value", targetValue.toString());
        }
    }

    requestAnimationFrame(frame);
}

document.querySelectorAll(".progress-circle").forEach(animateProgressCircle);
