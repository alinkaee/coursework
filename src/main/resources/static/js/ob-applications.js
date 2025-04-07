// job-applications.js
document.querySelectorAll('button:contains("ОТКЛИКНУТЬСЯ")').forEach(button => {
    button.addEventListener('click', async function() {
        const vacancyCard = this.closest('.main__search__block__develop__cards__row__card');
        const vacancyName = vacancyCard.querySelector('h4').textContent;

        // Получаем email из JWT (пример для cookie)
        const userEmail = getEmailFromJWT(); // Нужно реализовать эту функцию

        const response = await fetch('/applications/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            },
            body: JSON.stringify({
                userEmail: userEmail,
                vacancyName: vacancyName,
                status: "PENDING"
            })
        });

        if (response.ok) {
            alert('Отклик успешно отправлен!');
        }
    });
});

function getEmailFromJWT() {
    // Реализация зависит от вашего способа хранения JWT
    // Пример для cookie:
    const cookie = document.cookie.split('; ').find(row => row.startsWith('jwt_token='));
    if (cookie) {
        const token = cookie.split('=')[1];
        // Декодируем JWT (без проверки подписи!)
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub; // email пользователя
    }
    return null;
}