![Static Badge](https://img.shields.io/badge/Java-17-blue)
![Static Badge](https://img.shields.io/badge/JavaFX-17-blue)
![Static Badge](https://img.shields.io/badge/Lombok-blue)
![Static Badge](https://img.shields.io/badge/Log4j-blue)
![Static Badge](https://img.shields.io/badge/Maven-blue)


# Сетевой чат (клиентская часть)
Учебный проект GeekBrains (клиент и [сервер](https://github.com/igojig/ServerConsole))


<p align="center">
    <img src="./github_samples/login.png" width="400">
    <img src="./github_samples/register_sample.png" width="360">
</p>

<p align="center">
    <img src="./github_samples/chat/chat_demo_1.png" width="500">&nbsp;
    <img src="./github_samples/chat/chat_demo_2.png" width="600">&nbsp;
    <img src="./github_samples/chat/chat_demo_3.png" width="600">&nbsp;
    <img src="./github_samples/chat/chat_demo_4.png" width="600">&nbsp;
</p> 

## Функционал проекта:

- загрузка с сервера истории сообщений при входе пользователя 
- отображение списка активных пользователей
- подсчет сообщений
- [контекстное меню](./github_samples/context_menu.png)
- отправка приватных сообщений конкретному пользователю
- очистка истории сообщений
- смена имени пользователя
- сохранение истории сообщений на сервере при выходе
- [регистрация нового пользователя](./github_samples/register_sample.png)
- [закрытие клиента при процедуре входа или регистрации при неактивности клиента в течении 60 сек. (время устанавливается на сервере)](./github_samples/exit_client.png)
- вывод лога в консоль
## Установка
````
git clone https://github.com/igojig/FxMessager_client
````

## Запуск
Для функционирования требуется запуск [серверной части](https://github.com/igojig/ServerConsole)
```
chcp 1251
mvn clean javafx:run
```

Или запускать из IDE.<br> Для запуска нескольких экземпляров приложения установить параметр:<br>
[Edit configuration->Modify options->Allow multiple instances](github_samples/edit_configuration.png)


## Создание исполняемого образа
```
mvn clean javafx:jlink
```

#### Параметры для входа
при старте серверной части создаются четыре пользователя:
```
[login]  [password]  [username]
   1         1          One
   2         2          Two
   3         3          Three
   4         4          Four
```
также есть возможность зарегистрировать нового пользователя


