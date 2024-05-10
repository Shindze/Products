# Добро пожаловать
Это проект "Products", в котором я буду создавать приложение для отображения получаемых в ответ на запрос продуктов.

# RoadMap
Сделано:
- [x] Архитектура MVVM
- [x] Кэширование продуктов
- [x] Загрузка первых 20 объектов
- [x] Поиск из бэкенда
- [X] Сортировка по категориям
- [x] Переход на экран каждого объекта
- [x] Загрузка следующих продуктов
- [x] Отображение дополнительной информации об объекте
- [x] Базовые отловы ошибок
- [x] Более глубокие отловы ошибок

В процессе:

- [ ] Рефактор

# Известные проблемы:

При отсутствии интернета и попытке обновить список продуктов, а также выбрать категорию, кэш ранее загруженных продуктов очищается

Иногда данные кэшируются несколько раз или не отображается дополнительная информация. Для временного решения предлагается сделать свайп и обновить кэши 

# Описание
Проект разрабатывается для прохождения вступительного тестирования на стажировку в ВК.

Данное приложение написано на языке программирования Kotlin и создано специально для мобильных устройств.
Основные требования для запуска:

* Устройство android с версией ОС 7+
* Сенсорный экран устройства
* Возможность получения доступа к интернету

# Соглашение о префиксах коммитов
Чтобы людям было легче следить за прогрессом разработки, а также для того, чтобы все значимые изменения было проще перечислять в логе обновлений, я требую, чтобы все коммиты имели один из следующих префиксов:
- **FIX**: исправление ошибок
- **ADD**: добавление новых функциональных возможностей
- **TEST**: добавление отсутствующих тестов или исправление существующих тестов
- **PERF**: повышение производительности приложения
- **UX**: изменения пользовательского интерфейса или способов взаимодействия с ним
- **REFACTOR**: изменение кода, которое не исправляет ошибку и не добавляет функцию

-----
Авторские права за использование исходника readme приладлежат : https://github.com/mirea-ninja/rtu-mirea-mobile
