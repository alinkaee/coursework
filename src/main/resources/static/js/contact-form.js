document.addEventListener('DOMContentLoaded', function() {
    console.log('Скрипт формы обратной связи инициализирован');

    const form = document.querySelector('.relationship__form');
    if (!form) {
        console.error('Форма не найдена на странице! Проверьте селектор');
        return;
    }

    const submitBtn = form.querySelector('button');
    if (!submitBtn) {
        console.error('Кнопка отправки не найдена в форме!');
        return;
    }

    submitBtn.addEventListener('click', function(e) {
        e.preventDefault();
        console.log('Начата обработка отправки формы');

        const formData = {
            name: form.querySelector('input[type="text"]').value,
            email: form.querySelector('input[type="email"]').value,
            phone: form.querySelector('input[type="text"]').value,
            message: form.querySelector('textarea').value
        };

        console.log('Сформированы данные для отправки:', {
            name: formData.name ? 'указано' : 'не указано',
            email: formData.email ? 'указан' : 'не указан',
            phone: formData.phone ? 'указан' : 'не указан',
            messageLength: formData.message.length
        });

        fetch('/api/contact', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            console.log('Статус ответа:', response.status);

            if (!response.ok) {
                return response.json().then(err => Promise.reject(err));
            }
            return response.json(); // Теперь сервер всегда возвращает JSON
        })
        .then(data => {
            console.log('Успешный ответ:', data);
            alert('Ваша заявка отправлена! Мы свяжемся с вами в ближайшее время.');
            form.reset();
        })
        .catch(error => {
            const errorMsg = error.error || 'Неизвестная ошибка';
            console.error('Ошибка:', {
                message: errorMsg,
                details: error
            });
            alert(`Ошибка: ${errorMsg}. Пожалуйста, попробуйте позже.`);
        });

    });

    console.log('Обработчик события для кнопки отправки успешно добавлен');
});