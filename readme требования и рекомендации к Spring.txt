Экосистема Spring

SPR-0. Общее
SPR-001
Используйте актуальные версии in-house библиотек и стартеров, если позволяет версия Spring Boot.

Критичность: Высокая

Последние версии in-house библиотек можно посмотреть на данной странице


SPR-002
Если код взят из другого проекта и выполняет какую-то атомарную функцию, которая потенциально будет и в других проектах, то рекомендуется обсудить с командой, как его лучше переиспользовать: вынести в отдельную библиотеку, сделать стартером или пока просто скопировать.

Критичность: Средняя

Цели/причины:

Централизация переиспользуемого кода предотвращает дублирование и упрощает его поддержку
Создание библиотек и стартеров обеспечивает единообразие решений
Обсуждение с командой помогает выбрать оптимальный подход и в том числе избежать преждевременной абстракции


SPR-003
Приложение из любой ветки должно безопасно запускаться с дефолтными конфигурациями запуска в большинстве рабочих окружений сотрудников Службы Разработки.

Критичность: Высокая

Исключениями могут служить только наличие/отсутствие специфичных сетевых доступов или токен для доступа в Vault.

Другими словами должно быть так у любого разработчика:

Стянул ветку
опционально перевыпустил токен Vault
Нажал Run / Debug
Прила успешно запустилась
нужные кэши прогрузились, "безопасные" джобы активны, мутирующие что-либо в хранилищах джобы неактивны, и т.д.
Цели/причины:

Упрощает онбординг новых разработчиков и ускоряет начало работы над проектом
Предотвращает случайные изменения данных в хранилищах при локальном запуске
Обеспечивает единообразие окружений и снижает количество проблем при запуске


SPR-004
В случаях, если для проверки работоспособности (только локально) необходимо изменение каких-либо параметров конфигурации (например, адреса сервисов, учетные записи, адрес внешнего сервиса и т.д.), необходимо переопределять эти параметры через переменные окружения IDE.

Критичность: Высокая

Нельзя менять конфигурационные файлы в проекте, т.к. это может привести к случайному коммиту этих изменений в репозиторий.

SPR-005¶
В случаях, когда сами задаем bean-name или qualifier в аннотациях @Bean или @Qualifier, то это название необходимо выносить в константы.

Критичность: Высокая

А из других сервисов при внедрении зависимости использовать эти же константы.

Цели/причины:

Использование констант предотвращает ошибки из-за опечаток в строках
Упрощает поиск бинов, которые используют конкретный qualifier
Упрощает рефакторинг, так как изменения нужно делать только в одном месте


SPR-006
Рекомендуется именовать метод, аннотированный @PostConstruct, полным называнием initialize().

Критичность: Высокая

Цели/причины:

Единообразное именование "служебных" методов не противоречит общим рекомендациям по стилю кода
Минимизирует возможные конфликты с методами init() в старых версиях Spring


SPR-007
Для внедрения зависимостей рекомендуется использовать constructor-injection вместо field-injection

Критичность: Высокая

Цели/причины:

Constructor-injection делает зависимости явными и обязательными, что улучшает тестируемость
Соответствует практикам Spring и обеспечивает более предсказуемое поведение
SPR-008¶
В классах с @Configuration не использовать внедрение зависимости на уровне всего класса.

Критичность: Высокая

Использовать внедрение зависимостей на уровне параметров фабричных методов с @Bean.

Цели/причины:

Внедрение через параметры методов делает зависимости явными и упрощает понимание конфигурации
Предотвращает проблемы с порядком инициализации и циклическими зависимостями

Несоответствующий код:
@Configuration
public class FooConfiguration {
    @Autowired private DataSource dataSource;

    @Bean
    public MyService myService() {
        return new MyService(this.dataSource);
    }
}

Соответствующий код:
@Configuration
public class FooConfiguration {
    @Bean
    public MyService myService(DataSource dataSource) {
        return new MyService(dataSource);
    }
}


SPR-009
Рекомендуется использовать @RequiredArgsConstructor из Lombok для бина в случаях, если конструктор бина используется только для внедрения зависимостей, и в нем не выполняется какая-либо логика, или вызов конструктора супер-класса.

Критичность: Высокая

Цели/причины:

@RequiredArgsConstructor автоматически генерирует конструктор, уменьшая шаблонный код
Упрощает поддержку кода и снижает вероятность ошибок при добавлении новых зависимостей

Несоответствующий код:
@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final PricePlanRepository pricePlanRepository;
    private final NotificationService notificationService;

    public SubscriberService(SubscriberRepository subscriberRepository,
                            PricePlanRepository pricePlanRepository,
                            NotificationService notificationService) {
        this.subscriberRepository = subscriberRepository;
        this.pricePlanRepository = pricePlanRepository;
        this.notificationService = notificationService;
    }
}


Соответствующий код:
@Service
@RequiredArgsConstructor
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final PricePlanRepository pricePlanRepository;
    private final NotificationService notificationService;
}


SPR-010
Конфигурационные параметры (в т.ч. хранящиеся в БД или другом внешнем хранилище), описывающие длительность, нужно хранить и использовать в формате java Duration.

Критичность: Высокая

Если нижележащий функционал (фреймворк, third-party библиотека) требует другого представления (секунды, миллисекунды и др.) необходимо на уровне Вашего разрабатываемого функционала произвести преобразование.

Если нижележащим функционалом является in-house библиотека, то рекомендуется оформить в Jira задачу-предложение по доработки библиотеки. После доработки библиотеки рекомендуется актуализировать свой функционал для использования измененных механизмов.

Цели/причины:

Duration обеспечивает типобезопасность и предотвращает ошибки при работе с единицами времени
Улучшает читаемость конфигурации и делает единицы измерения явными
Упрощает преобразования между различными представлениями времени

Несоответствующий код:
@ConfigurationProperties(prefix = "subscriber")
public class SubscriberProperties {
    private long sessionTimeoutSeconds = 3600;
    private int cacheExpirationMinutes = 30;
    private long retryDelayMillis = 5000;
}

@Service
public class SessionService {
    private final SubscriberProperties properties;

    public void configureSession() {
        long timeout = properties.getSessionTimeoutSeconds() * 1000; // преобразование в миллисекунды
        // ...
    }
}

Соответствующий код:
@ConfigurationProperties(prefix = "subscriber")
public class SubscriberProperties {
    private Duration sessionTimeout = Duration.ofHours(1);
    private Duration cacheExpiration = Duration.ofMinutes(30);
    private Duration retryDelay = Duration.ofSeconds(5);
}

@Service
public class SessionService {
    private final SubscriberProperties properties;

    public void configureSession() {
        long timeoutMillis = properties.getSessionTimeout().toMillis();
        // ...
    }
}


SPR-011
Необходимо контролировать предоставляемые Actuator эндпоинты. Не все эндпоинты должны быть доступны в развернутом приложении, особенно в продакшене.

Критичность: Блокирующая

Необходимо либо явно указывать предоставляемые эндпоинты, либо в крайнем случае, отключить все эндпоинты, которые могут быть использованы для негативного влияния на приложения или для получения несанкционированного доступа к данным приложения.

Цели/причины:

Некоторые Actuator эндпоинты могут раскрывать конфиденциальную информацию или позволять управлять приложением
Явное указание доступных эндпоинтов улучшает безопасность и соответствует принципу минимальных привилегий
Предотвращает несанкционированный доступ к данным и управлению приложением (например, получив heapdump, можно найти в нем пароли от БД или других сервисов)
Рекомендуется отключить следующие эндпоинты:


management:
  endpoints:
    web:
      exposure:
        exclude: shutdown,startup,env,configprops,heapdump,threaddump



SPR-1. Data access Layer
SPR-100
Для каждого пула соединений к БД переопределять значение сессии program application-name Вашего приложения, чтобы было проще определять систему-источник запросов в дальнейшем (например, в AWR-отчетах)

Критичность: Высокая

Это можно сделать через свойства Hikari DataSource:

Если пул иницализируется вручную, то


hikariConfig.addDataSourceProperty("v$session.program", applicationName.substring(0, Math.min(applicationName.length(), 60)));
Если через spring-data

spring:
  datasource:
    # ....
    hikari:
      data-source-properties:
        "[v$session.program]": ${spring.application.name}


SPR-101
Необходимо явно прописывать pool-name.

Критичность: Высокая

Для унификации использовать такие имена для пулов, которые уже есть в новых проектах: - slrdb-lk-auth-pool - slrdb-crm-platform-pool - slrdb-flow-service-pool - redknee-db-pool - loading-subscriber-pool

Note

Для не указанных в списке БД и схем рекомендуется формировать имя пула по принципу <код/имя бд которое всем понятно и знакомо>-<схема>-pool

Главное, чтобы приложения согласовано использовали одинаковые названия при работе с одной и той же схемой.

Цели/причины:

Явное указание pool-name упрощает мониторинг и построение графов зависимостей между сервисами и БД


SPR-102
Рекомендуется добавлять и корректно устанавливать (н-р, через @PrePersist, @PreUpdate) Дату создания и Дату изменения записей.

Критичность: Средняя

Исключением являются таблицы, которые предназначены только для ManyToMany связей.

Цели/причины:

Упрощает отладку и анализ изменений данных в системе
Не требует от разработчиков дополнительных усилий для установки этих полей вручную

Соответствующий код:
@Entity
@Table(name = "subscribers")
public class SubscriberEntity {
    @Id
    private Long id;
    private String phoneNumber;
    private BigDecimal balance;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}


SPR-103
Сбалансировано выбирать подход при использовании нативных/jpql запросов и генерируемых по имени метода в jpa-репозиториях.

Критичность: Средняя

Если название метода получается длиннее 50 символов или производит поиск более чем по 3 параметрам, то стоит выбрать хотя бы jpql для улучшения читаемости и восприятия другими разработчиками.

Готовый sql-like запрос разработчику понять быстрее, чем в голове строить итоговый запрос по длинному имени метода.


SPR-104
В Spring Data репозиториях названия параметров в @Param при нативных запросах должны быть только в snake_case

Критичность: Высокая

Цели/причины:

snake_case соответствует соглашениям SQL и улучшает читаемость запросов
Обеспечивает единообразие именования параметров в запросах

Несоответствующий код:
@Repository
public interface SubscriberRepository extends JpaRepository<SubscriberEntity, Long> {
    @Query(value = "SELECT * FROM subscribers WHERE phone_number = :phoneNumber AND is_active = :isActive", 
           nativeQuery = true)
    List<SubscriberEntity> findByPhoneNumberAndActive(@Param("phoneNumber") String phoneNumber, 
                                                      @Param("isActive") Boolean isActive);

    @Query(value = "SELECT * FROM subscribers WHERE balance > :minBalance", nativeQuery = true)
    List<SubscriberEntity> findWithBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);
}

Соответствующий код:
@Repository
public interface SubscriberRepository extends JpaRepository<SubscriberEntity, Long> {
    @Query(value = "SELECT * FROM subscribers WHERE phone_number = :phone_number AND is_active = :is_active", 
           nativeQuery = true)
    List<SubscriberEntity> findByPhoneNumberAndActive(@Param("phone_number") String phoneNumber, 
                                                      @Param("is_active") Boolean isActive);

    @Query(value = "SELECT * FROM subscribers WHERE balance > :min_balance", nativeQuery = true)
    List<SubscriberEntity> findWithBalanceGreaterThan(@Param("min_balance") BigDecimal minBalance);
}


SPR-105
Не рекомендуется работать с jpa-энтити вне сервисов, которые выбирают данные из хранилища (т.е. вне тех, которые обращаются непосредственно к репозиторию).

Критичность: Высокая

При необходимости передачи данных из jpa-энтити в вышестоящие/горизонтальные слои использовать модели.

Цели/причины:

Изоляция JPA-сущностей в слое доступа к данным улучшает разделение ответственности
Предотвращает утечку деталей реализации БД в бизнес-логику


SPR-106
Т.к. с энтити мы работаем только в "доменных" сервисах, а в остальных слоях используем модели, то нецелесообразно только в одном месте использовать User для энтити, а в куче других мест постоянно писать UserModel, хотя для этих функционалов абсолютно неважно, откуда пришли данные - из БД или откуда-то еще.

Критичность: Высокая

Поэтому настоятельно рекомендуется энтити именовать с суффиксом Entity. А модели без какого-либо суффикса.

Несоответствующий код:
@Entity
@Table(name = "subscribers")
public class Subscriber {
    @Id
    private Long id;
    private String phoneNumber;
}

public class SubscriberModel {
    private Long id;
    private String phoneNumber;
}

@Service
public class SubscriberService {
    public SubscriberModel getSubscriber(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id);
        return convertToModel(subscriber);
    }
}

Соответствующий код:
@Entity
@Table(name = "subscribers")
public class SubscriberEntity {
    @Id
    private Long id;
    private String phoneNumber;
}

public class Subscriber {
    private Long id;
    private String phoneNumber;
}

@Service
public class SubscriberService {
    public Subscriber getSubscriber(Long id) {
        SubscriberEntity subscriberEntity = subscriberRepository.findById(id);
        return convertToModel(subscriberEntity);
    }
}


SPR-107
При задании mappedBy аннотации @OneToMany рекомендуется использовать названия полей, генерируемых Lombok-ом с помощью аннотации @FieldNameConstants, а не писать их в виде строковых литералов.

Критичность: Высокая

Цели/причины:

Использование констант предотвращает ошибки из-за опечаток в строках
Обеспечивает рефакторинг-безопасность: при переименовании поля константа обновится автоматически


SPR-108
При работе с нативными запросами вне Spring Data репозиториев рекомендуется использовать NamedParameterJdbcTemplate

Критичность: Высокая

Цели/причины:

NamedParameterJdbcTemplate обеспечивает безопасную работу с параметрами через именованные плейсхолдеры
Улучшает читаемость запросов по сравнению с позиционными параметрами
Предотвращает ошибки при изменении порядка параметров и упрощает поддержку кода


SPR-109
При работе с нативными запросами вне Spring Data репозиториев и использовании RowMapper-ов, рекомендуется обращаться к колонкам результата по названиям колонок, а не по индексам.

Критичность: Высокая

Цели/причины:

Обращение по именам колонок улучшает читаемость и делает код более понятным
Предотвращает ошибки при изменении порядка колонок в SELECT-запросе
Упрощает рефакторинг и поддержку кода, так как имена колонок более информативны

Несоответствующий код:
@Service
public class SubscriberReportService {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<SubscriberReport> generateReport() {
        String sql = "SELECT id, phone_number, balance, created_date FROM subscribers";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SubscriberReport report = new SubscriberReport();
            report.setId(rs.getLong(1)); // обращение по индексу
            report.setPhoneNumber(rs.getString(2));
            report.setBalance(rs.getBigDecimal(3));
            report.setCreatedDate(rs.getTimestamp(4).toLocalDateTime());
            return report;
        });
    }
}


Соответствующий код:
@Service
public class SubscriberReportService {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<SubscriberReport> generateReport() {
        String sql = "SELECT id, phone_number, balance, created_date FROM subscribers";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SubscriberReport report = new SubscriberReport();
            report.setId(rs.getLong("id"));
            report.setPhoneNumber(rs.getString("phone_number"));
            report.setBalance(rs.getBigDecimal("balance"));
            report.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
            return report;
        });
    }
}


SPR-110
Не следует использовать конкатенацию строк для формирования динамических запросов к БД, особенно если в них участвуют параметры, полученные от клиента/пользователя.

Критичность: Блокирующая

Данный подход открывает возможность для SQL-инъекций.

Правильный подход - использовать параметры запроса или ORM-фреймворки.

Несоответствующий код:
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
jdbcTemplate.query(sql, ...);

Соответствующий код:
String sql = "SELECT * FROM users WHERE username = :username";
MapSqlParameterSource params = new MapSqlParameterSource();
params.addValue("username", username);
namedParameterJdbcTemplate.query(sql, params, ...);


SPR-111
Внимательно следить за последовательностью вывозов методов с @Transactional.

Критичность: Высокая

А именно помнить, что аннотация @Transactional работает только при вызове методов извне бина (через прокси). А при вызове метода внутри того же бина транзакция не будет создана, как могло бы ожидаться.


SPR-112
Не рекомендуется мутировать состояние jpa-энтити без дальнейшего явного вызова методов сохранения.

Критичность: Высокая

Потому как может произойти ситуация, когда в рамках @Transactional hibernate сохранит изменения в БД.

В целях единообразия не рекомендуется этого делать также и для spring-data-jdbc сущностей.



SPR-2. Контроллеры
SPR-201
Контроллеры должны быть разделены по функциональной ответственности и сценарию использования: не следует объединять в одном контроллере роуты для разных логически выделяемых групп действий.

Критичность: Высокая

Для каждой такой группы должен создаваться отдельный контроллер.

Например: Есть пользовательские операции, связанные с профилем пользователя: просмотр профиля, обновление данных, загрузка аватара.

И есть административные операции управления ролями и правами: назначение ролей, просмотр прав, аудит изменений.

Если все эти методы собраны в одном контроллере, возникают смешение ответственности. Правильный подход в данном случае - вынести операции профиля в один контроллер, а управление ролями в другой, чтобы чётко разделить сценарии и упростить сопровождение и развитие.

Несоответствующий код:
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {
    @GetMapping("/{id}")
    public Subscriber getSubscriber(@PathVariable Long id) {
        // получение абонента
    }

    @PostMapping("/{id}/activate")
    public void activateSubscriber(@PathVariable Long id) {
        // активация абонента
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable Long id) {
        // получение баланса
    }

    @PostMapping("/{id}/block")
    public void blockSubscriber(@PathVariable Long id) {
        // блокировка абонента
    }

    @GetMapping("/reports/monthly")
    public MonthlyReport getMonthlyReport() {
        // генерация месячного отчета
    }

    @PostMapping("/reports/export")
    public void exportReport(@RequestBody ReportRequest request) {
        // экспорт отчета
    }
}

Соответствующий код:
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {
    @GetMapping("/{id}")
    public Subscriber getSubscriber(@PathVariable Long id) {
        // получение абонента
    }

    @PostMapping("/{id}/activate")
    public void activateSubscriber(@PathVariable Long id) {
        // активация абонента
    }

    @PostMapping("/{id}/block")
    public void blockSubscriber(@PathVariable Long id) {
        // блокировка абонента
    }
}

@RestController
@RequestMapping("/api/subscribers/{subscriberId}/balance")
public class SubscriberBalanceController {
    @GetMapping
    public BigDecimal getBalance(@PathVariable Long subscriberId) {
        // получение баланса
    }

    @PostMapping("/top-up")
    public void topUpBalance(@PathVariable Long subscriberId, @RequestBody TopUpRequest request) {
        // пополнение баланса
    }
}

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @GetMapping("/monthly")
    public MonthlyReport getMonthlyReport() {
        // генерация месячного отчета
    }

    @PostMapping("/export")
    public void exportReport(@RequestBody ReportRequest request) {
        // экспорт отчета
    }
}


SPR-202
Контроллеры должны быть тонкими, т.е. работать только с параметрами запроса и ответами, и вызывать сервисы для выполнения бизнес-логики.

Критичность: Высокая

Цели/причины:

Тонкие контроллеры улучшают разделение ответственности между слоями приложения
Делают контроллеры более понятными и фокусированными на обработке HTTP-запросов

Несоответствующий код:
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {
    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PricePlanRepository pricePlanRepository;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/{id}/activate")
    public void activateSubscriber(@PathVariable Long id) {
        SubscriberEntity subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException("Subscriber not found"));

        if (!subscriber.isActive()) {
            subscriber.setActive(true);
            subscriberRepository.save(subscriber);

            PricePlanEntity pricePlan = pricePlanRepository.findById(subscriber.getPricePlanId())
                    .orElse(null);

            if (pricePlan != null) {
                String message = "Ваш тарифный план " + pricePlan.getName() + " активирован";
                notificationService.sendSms(subscriber.getPhoneNumber(), message);
            }
        }
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable Long id) {
        SubscriberEntity subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException("Subscriber not found"));

        BigDecimal balance = subscriber.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balance = BigDecimal.ZERO;
        }

        return balance;
    }
}


Соответствующий код:
@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
public class SubscriberController {
    private final SubscriberService subscriberService;

    @PostMapping("/{id}/activate")
    public void activateSubscriber(@PathVariable Long id) {
        subscriberService.activateSubscriber(id);
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable Long id) {
        return subscriberService.getSubscriberBalance(id);
    }
}

@Service
@RequiredArgsConstructor
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final PricePlanRepository pricePlanRepository;
    private final NotificationService notificationService;

    public void activateSubscriber(Long id) {
        SubscriberEntity subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException("Subscriber not found"));

        if (!subscriber.isActive()) {
            subscriber.setActive(true);
            subscriberRepository.save(subscriber);

            PricePlanEntity pricePlan = pricePlanRepository.findById(subscriber.getPricePlanId())
                    .orElse(null);

            if (pricePlan != null) {
                String message = "Ваш тарифный план " + pricePlan.getName() + " активирован";
                notificationService.sendSms(subscriber.getPhoneNumber(), message);
            }
        }
    }

    public BigDecimal getSubscriberBalance(Long id) {
        SubscriberEntity subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException("Subscriber not found"));

        BigDecimal balance = subscriber.getBalance();
        return balance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balance;
    }
}


SPR-203
Параметры контроллеров, являющиеся POJO-моделями, должны быть аннотированы @Valid для запуска валидации. А также поля самих модели должны быть аннотированы необходимыми аннотациями валидации из javax.validation.

Критичность: Высокая

Т.о. контроллер максимально должен гарантировать, что пришедшие параметры валидны с точки зрения формата и контракта предоставляемого rest-api.

Цели/причины:

Валидация на уровне контроллера обеспечивает раннее обнаружение некорректных данных
Предотвращает попадание невалидных данных в бизнес-логику


SPR-204
В большинстве случаев не рекомендуется возвращать из контроллеров ResponseEntity, т.к. код в контроллере должен представлять собой успешные сценарии, а все неуспешные - обрабатываться через глобальный exception-handler.

Критичность: Средняя

В противном случае во всех местах для успешного выполнения метода контроллера мы только и будем писать return ResponseEntity.ok(...), что вносит избыточность.

Note:
Исключением могут быть случаи, когда роут нужен для возврата файлов.


SPR-205
Все неуспешные выполнения методов контроллеров должны быть обработаны либо в глобальном exception-handler-е, либо в exception-handler-е специфичном для контроллера.

Критичность: Высокая

Цели/причины:

Обработка всех исключений обеспечивает предсказуемое поведение API и корректные HTTP-ответы
Упрощает логирование ошибок и мониторинг приложения


SPR-206
Параметры контроллеров, представляющие собой узкоспециализированные идентификаторы (номер телефона, ИНН) с заранее известным форматом, должны быть проверены на валидность формата с помощью кастомных валидаторов javax.validation

Критичность: Средняя

Цели/причины:

Обеспечивают единообразную валидацию и понятные сообщения об ошибках для клиентов API


SPR-207
Все роуты (кроме Актуатора) должны быть закрыты под аутентификацией.

Критичность: Блокирующая

При m2m взаимодействии для аутентификации необходимо использовать hmac-подпись запросов.

Для hmac лучше использовать имеющийся стартер simple-route-guard


SPR-208
Не рекомендуется использовать Basic-аутентификацию, т.к. этот способ основан на передаче имени пользователя и пароля в кодировке Base64.

Критичность: Блокирующая

Поскольку Base64 легко распознать и восстановить, таким образом возможна утечка конфиденциальных данных.


SPR-209
Рекомендуется логировать корреляционный идентификатор для трассировки цепочки запросов: если во входящем запросе уже присутствует trace-id/correlation-id (или аналог с другим именем) - сохранить и использовать его; если отсутствует - сгенерировать новый.

Критичность: Высокая

При вызове внешних или внутренних систем следует передавать этот идентификатор в заголовках запроса, используя согласованное с командой другого проекта имя заголовка и формат.

Цели/причины:

Корреляционные идентификаторы в распределенных системах упрощают отладку и анализ проблем, позволяя связать логи различных сервисов
SPR-210¶
Каждый метод контроллера должен быть описан в OpenAPI документации.

Критичность: Высокая

Рекомендуется использовать in-house стартер, описанный в данной документации

Также рекомендуется использовать дополнительные механизмы по декларативному дополнению информации в OpenAPI документации, которые предоставляет вышеуказанный стартер.


SPR-211
Рекомендуется отделять описание для OpenAPI документации от кода контроллера в отдельные интерфейсы.

Критичность: Средняя

Название интерфейса рекомендуется делать по шаблону <ИмяКонтроллера>Api.

Цели/причины:

Разделение документации и реализации улучшает читаемость контроллеров, не загромождая их деталями документации


SPR-212
Рекомендуется при подготовке описания OpenAPI максимально использовать возможности Swagger по извлечению метаданных из сигнатур методов контроллера.

Критичность: Средняя

Цели/причины:

Автоматическое извлечение метаданных снижает дублирование и упрощает поддержку документации
Обеспечивает синхронизацию документации с кодом и снижает риск расхождений
Уменьшает объем ручной работы и улучшает консистентность документации

Note:
Однако, не следует забывать о единообразии подходов, применяемых в проекте. Если в проекте используется подход, противоречащий данному правилу и не описанный явно в README, то следует запланировать рефакторинг в рамках отдельной задачи. Если рефакторинг невозможен в силу обоснованных причин, то следует явно зафиксировать эти причины в README проекта.


Несоответствующий код:
@Parameter(name = "serviceId", description = "id информационной услуги", in = ParameterIn.QUERY, schema = @Schema(implementation = Long.class))
ServiceDetail getInfoServiceDetail(@RequestSubscriberInfo SubscriberInfo info,
                                   @RequestParam Long serviceId);

Соответствующий код:
ServiceDetail getInfoServiceDetail(@RequestSubscriberInfo SubscriberInfo info,
                                   @Parameter(description = "id информационной услуги") @RequestParam Long serviceId);


SPR-213¶
HTTP-методы рекомендуется использоваться в соответствии с их семантикой: для каждого типа операции следует выбирать подходящий глагол.

Критичность: Средняя

Метод	Назначение
GET	Получение данных
POST	Создание ресурса
PUT	Полное обновление ресурса
PATCH	Частичное обновление ресурса
DELETE	Удаление ресурса
Цели/причины:

Корректное использование HTTP-методов делает контракт API предсказуемым и понятным без чтения документации
Семантика методов влияет на поведение кэширования, повторных запросов и промежуточного ПО (прокси, шлюзы)

Info:
Допускается отклонение от правила в случаях, если переносим роуты из SLR в новое приложение и должны сохранить совместимость с мобильным приложением


SPR-214
Контроллер должен возвращать HTTP-статус, соответствующий результату операции, а не всегда 200 OK с признаком ошибки в теле ответа.

Критичность: Высокая

Цели/причины:

Корректные статусы позволяют клиентам и мониторингу единообразно отличать успех от ошибки
Уменьшают объём кастомной логики разбора ответа на стороне клиента

Несоответствующий код
Соответствующий код

HTTP/1.1 200 OK
{ "success": false, "error": "Subscriber not found" }

Info:
Допускается отклонение от правила в случаях, если переносим роуты из SLR в новое приложение и должны сохранить совместимость с мобильным приложением

SPR-215
Операции API следует проектировать с учётом идемпотентности: повторное выполнение GET, PUT, DELETE не должно приводить к иному результату, чем однократное.

Критичность: Высокая

Для неидемпотентных по своей природе операций (например, POST, создающего ресурс) при необходимости защиты от дублей следует предусматривать механизм идемпотентности (например, ключ идемпотентности в заголовке запроса).

Цели/причины:

Идемпотентность критична при повторных запросах из-за сетевых сбоев и ретраев
Предотвращает дублирование данных и непреднамеренные побочные эффекты


SPR-216
Для базовых CRUD-операций ресурсы в URL должны именоваться существительными во множественном числе; использование глаголов в пути ресурса не допускается.

Критичность: Средняя

Действие над ресурсом выражается HTTP-методом, а не словом в URL.

Несоответствующий код:
GET  /getUsers
POST /createUser

Соответствующий код:
GET  /users
GET  /users/{id}
POST /users


SPR-217
Для связанных ресурсов рекомендуется использовать иерархию в пути URL вместо передачи родительского идентификатора через query-параметр.

Критичность: Средняя

Несоответствующий код:
GET /payments/123?userId=555

Соответствующий код:
GET /users/555/payments/123

Info

Допускается отклонение от правила в случаях, если переносим роуты из SLR в новое приложение и должны сохранить совместимость с мобильным приложением


SPR-218
Фильтрацию, сортировку и пагинацию, используемые для фронтенд веб-приложений, рекомендуется передавать через query-параметры, а не кодировать в пути ресурса или в теле запроса.

Критичность: Высокая

Для коллекций, способных вырасти до большого объёма, пагинация обязательна, чтобы избежать выгрузки всех записей одним ответом.


Несоответствующий код:
GET /products/expensive/sortedByPrice

Соответствующий код:
GET /products?category=new&sort=name&page=0&size=20


SPR-220
В публичном контракте API не следует использовать внутренние термины реализации.

Имена ресурсов должны быть понятны потребителю API.

Критичность: Высокая


Несоответствующий код:
GET /cdc

Соответствующий код:
GET /changes


SPR-221
Тело ответа об ошибке должно иметь единообразную структуру и содержать машиночитаемый код ошибки и понятное человеку сообщение.

Стек-трейс и внутренние детали реализации в тело ответа включаться не должны.

Критичность: Высокая

Рекомендуется возвращать идентификатор трассировки (trace-id/correlation-id) в теле ответа или в заголовке для последующего поиска проблемы в логах.

Несоответствующий код:
{ "error": "Что-то пошло не так" }

Соответствующий код:
{ "code": "PRODUCT_NOT_FOUND", "message": "Продукт не найден", "trace_id": "b3f1c2d4" }



SPR-3. Джобы
SPR-301
Любой джоб, изменяющий данные, должен отключаться как-минимум через @ConditionalOnProperty со значением указанной проперти false по-умолчанию.

Критичность: Блокирующая

Цели/причины:

Отключение мутирующих джобов по умолчанию предотвращает случайные изменения данных в хранилищах при локальном запуске приложения
Позволяет явно контролировать выполнение джобов через конфигурацию

Несоответствующий код:
@Component
@RequiredArgsConstructor
public class InactiveSubscriberCleaner {
    private final SubscriberRepository subscriberRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteInactiveSubscribers() {
        List<SubscriberEntity> inactiveSubscribers = subscriberRepository
                .findInactiveSubscribersOlderThan(LocalDateTime.now().minusYears(1));
        subscriberRepository.deleteAll(inactiveSubscribers);
    }
}

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.subscriber.balance-updater.enabled", havingValue = "true")
public class SubscriberBalanceUpdater {
    private final SubscriberRepository subscriberRepository;

    @Scheduled(fixedRate = 60000)
    public void updateBalances() {
        // обновление балансов абонентов
    }
}

Соответствующий код:
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.subscriber.inactive-cleaner.enabled", havingValue = "true", matchIfMissing = false)
public class InactiveSubscriberCleaner {
    private final SubscriberRepository subscriberRepository;

    @Scheduled(cron = "${app.subscriber.inactive-cleaner.cron}")
    public void deleteInactiveSubscribers() {
        List<SubscriberEntity> inactiveSubscribers = subscriberRepository
                .findInactiveSubscribersOlderThan(LocalDateTime.now().minusYears(1));
        subscriberRepository.deleteAll(inactiveSubscribers);
    }
}

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.subscriber.balance-updater.enabled", havingValue = "true", matchIfMissing = false)
public class SubscriberBalanceUpdater {
    private final SubscriberRepository subscriberRepository;

    @Scheduled(fixedDelayString = "${app.subscriber.balance-updater.execution-interval-duration}")
    public void updateBalances() {
        // обновление балансов абонентов
    }
}



SPR-302
Периодичность/крон-выражение выполнения джоба обязательно должно быть вынесено в application.yml

Критичность: Высокая

Цели/причины:

Вынесение расписания в конфигурацию позволяет изменять его без перекомпиляции кода
Упрощает управление расписанием в различных окружениях (staging, prod), если необходимо

Несоответствующий код:
@Component
@RequiredArgsConstructor
public class CallHistoryArchiver {
    private final CallHistoryRepository callHistoryRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void archiveOldCallHistory() {
        // архивация старой истории звонков
    }
}

Соответствующий код:
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.call-history.archiver.enabled", havingValue = "true", matchIfMissing = false)
public class CallHistoryArchiver {
    private final CallHistoryRepository callHistoryRepository;

    @Scheduled(cron = "${app.call-history.archiver.cron}")
    public void archiveOldCallHistory() {
        // архивация старой истории звонков
    }
}

app:
  call-history:
    archiver:
      enabled: false
      cron: "0 0 3 * * *"


SPR-303
Значения, передаваемые в аннотацию @Scheduled для fixedRateString, fixedDelayString, initialDelayString, должны быть объявлены в формате java Duration.

Критичность: Высокая

Цели/причины:

Использование Duration обеспечивает типобезопасность и предотвращает ошибки при работе с единицами времени
Улучшает читаемость конфигурации и делает единицы измерения явными
Упрощает преобразования и обеспечивает единообразие в работе со временем

Несоответствующий код:
@Component
@RequiredArgsConstructor
public class BalanceChecker {
    private final SubscriberService subscriberService;

    @Scheduled(fixedDelayString = "3600", initialDelayString = "60") // секунды
    public void syncSubscriberData() {
        // синхронизация данных абонентов
    }
}

Соответствующий код:
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.subscriber.balance-checker.enabled", havingValue = "true", matchIfMissing = false)
public class BalanceChecker {
    private final SubscriberService subscriberService;

    @Scheduled(fixedDelayString = "${app.subscriber.sync.execution-interval-duration}",
               initialDelayString = "${app.subscriber.sync.initial-delay-duration}")
    public void syncSubscriberData() {
        // синхронизация данных абонентов
    }
}

app:
    sync:
      enabled: false
      execution-interval-duration: PT1H
      initial-delay-duration: PT1M


SPR-304
Для джобов, выполняющихся по крону, рекомендуется реализовывать проверку на то, что джоб уже/еще выполняется

Критичность: Высокая

Цели/причины:

Защищает от проблем при длительном выполнении джоба, когда следующий запуск может начаться до завершения предыдущего
Пример реализации проверки

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.ext-gateways-requests-history", name = "enabled", havingValue = "true")
public class OutdatedExtGatewaysRequestsCleaner {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Scheduled(initialDelayString = "${app.ext-gateways-requests-history.execution-interval-duration}",
            fixedDelayString = "${app.ext-gateways-requests-history.execution-interval-duration}")
    public void execute() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("[EXT-GATEWAYS REQUESTS] :: Previous Cleaning task is still running. Skipping this execution.");
            return;
        }

        try {
            // ... logic here ...
        } catch (Exception e) {
            // ... error handling here ...
        } finally {
            isRunning.set(false);
        }
    }
}


SPR-305
Всегда есть ненулевая вероятность, что джоб может запуститься параллельно на нескольких инстансах приложения, что может привести к негативным последствиям (например, двойное списание средств, дублирование записей и т.п).

Критичность: Блокирующая

Поэтому рекомендуется:

либо обеспечивать безопасность параллельного выполнения на уровне бизнес-логики (когда прила умеет честно работать с джобами на нескольких инстансах),
либо использовать распределенные блокировки. На данный момент в Службе чаще всего используется распределенная блокировка через Zookeeper посредством библиотеки kg.nurtelecom.nur-toys.zookeer-client-starter



SPR-4. REST обращения к другим сервисам
SPR-401
Если используется обращение по ресту и Spring Boot 3.2.5+, то рекомендуется использовать готовый стартер communication-rest-client

Критичность: Высокая

Цели/причины:

Использование готового стартера обеспечивает единообразие подходов к REST-коммуникации между сервисами
Упрощает реализацию и снижает количество шаблонного кода
Предоставляет встроенные механизмы логирования и метрик


SPR-402
Если нет возможности использовать communication-rest-client, то используемый рест-темплейт в метрики Prometheus должен предоставлять кастомыне тэги в дополнение к базовым тэгам метрики http.clients:

Критичность: Высокая

connector.code
connector.target
metric.version
Цели/причины:

Унифицированный формат метрик позволяет использовать уже настроенные дашборды и алерты


SPR-403
При использовании path-variables в uri НЕЛЬЗЯ передавать сконкатенированную итоговую строку в RestTemplate, т.к. в этом случае метрики не смогут корректно выделить переменные части url.

Критичность: Блокирующая

Вместо этого необходимо передавать Map параметров, чтобы сам RestTemplate сделал потом buildAndExpand.


Несоответствующий код:
@Service
@RequiredArgsConstructor
public class BillingServiceClient {
    private final RestTemplate restTemplate;

    public SubscriberBalance getSubscriberBalance(Long subscriberId) {
        String url = "http://billing-service/api/subscribers/" + subscriberId + "/balance";
        return restTemplate.getForObject(url, SubscriberBalance.class);
    }

    public void updatePricePlan(Long subscriberId, Long pricePlanId) {
        String url = String.format("http://billing-service/api/subscribers/%d/price-plans/%d", 
                                  subscriberId, pricePlanId);
        restTemplate.put(url, null);
    }
}

Соответствующий код

@Service
@RequiredArgsConstructor
public class BillingServiceClient {
    private final RestTemplate restTemplate;

    public SubscriberBalance getSubscriberBalance(Long subscriberId) {
        String url = "http://billing-service/api/subscribers/{subscriber_id}/balance";
        Map<String, Long> uriVariables = Map.of("subscriber_id", subscriberId);
        return restTemplate.getForObject(url, SubscriberBalance.class, uriVariables);
    }

    public void updatePricePlan(Long subscriberId, Long pricePlanId) {
        String url = "http://billing-service/api/subscribers/{subscriber_id}/price-plans/{price_plan_id}";
        Map<String, Long> uriVariables = Map.of(
            "subscriber_id", subscriberId,
            "price_plan_id", pricePlanId
        );
        restTemplate.put(url, null, uriVariables);
    }
}


SPR-404
Если обращение к внешнему сервису затрагивает важные данные, то лучше логировать запросы и ответы во внешнее хранилище.

Критичность: Высокая

Также рекомендуется использовать хранимые логи запросов при обращении к сервисам, которые сопровождаются и разрабатываются НЕ Службой Разработки (т.к. отсутсвие логов затрудняет разбор проблем при взаимодействии с другими компаниями/дирекциями).

Если используется communication-rest-client, то для этого лучше подключить дополнительный стартер logging-kafka для централизованного логирования через Kafka


SPR-405
Для хранения секретов от внешних REST-сервисах при использовании communication-rest-client рекомендуется использовать стартер secrets-vault-provider для хранения секретов в Vault

Критичность: Блокирующая

Цели/причины:

Хранение секретов в Vault обеспечивает безопасное управление конфиденциальными данными
Предотвращает попадание секретов в код и конфигурационные файлы, что критично для безопасности
Обеспечивает централизованное управление секретами и упрощает их ротацию



SPR-5. Кэширование
SPR-501
Если для функционала вводится кэширование, одновременно должен быть спроектирован и реализован механизм инвалидации (evict) кэша.

Кэширование без определённой стратегии сброса не допускается.

Критичность: Высокая

Стратегия инвалидации должна быть выбрана явно: по событию изменения данных (@CacheEvict/@CachePut), по истечению TTL, либо их комбинация.

Цели/причины:

Кэш без механизма инвалидации приводит к отдаче устаревших данных
Заранее спроектированный evict дешевле, чем встраивание сброса в уже работающий кэш



SPR-9. Логирование
SPR-901
Не использовать для вывода информации в логи System.out, для логирования всегда необходимо использовать библиотеки логирования

Критичность: Блокирующая

Цели/причины:

System.out не позволяет контролировать уровень логирования и не интегрируется с системами сбора логов
Обеспечивает единообразие логирования и упрощает анализ и отладку приложения


SPR-902
Не следует использовать конкатенацию строк для формирования сообщений в логах при использовании SLF4J, Log4j

Критичность: Средняя

Цели/причины:

Использование параметризованных сообщений улучшает производительность за счёт отложенного формирования строки
Упрощает читаемость кода и уменьшает вероятность ошибок при форматировании сообщений

Несоответствующий код:
logger.info("User " + userId + " has logged in from IP " + ipAddress);


Соответствующий код:
logger.info("User {} has logged in from IP {}", userId, ipAddress);


SPR-903
В рамках одного функционала рекомендуется придерживаться одного формата сообщения в логах

Критичность: Средняя

Цели/причины:

Единообразный формат упрощает поиск и фильтрацию логов по функциональности
Улучшает читаемость и понимание последовательности операций

Соответствующий код:
[order-processing] :: Собираем заявки для обработки...
[order-processing] :: Найдено 10 заявок на обработку
[order-processing] :: Заявка 999 :: Делаем нужные действия...
[order-processing] :: Заявка 999 :: Всё хорошо
[order-processing] :: Три-четыре, закончили упражнения с заявками


SPR-904
Если не используется структурное логирование, то в начале каждого сообщения лога рекомендуется выводить идентификатор сущности/записи/объекта, с которым работаем.

Критичность: Высокая

Желательно, чтобы этот идентификатор отражал запись в рамках Домена приложения. То есть та информация, которая может прийти в заявке на DevSupport.

Например, для Пользователя логично, что таким идентификатором будет логин, для Абонента - желательно номер телефона, ну или BAN.

Цели/причины:

Идентификатор в начале сообщения упрощает фильтрацию логов по конкретной сущности
Позволяет быстро отследить все операции с объектом в рамках одного запроса

Соответствующий код

[996700999999] :: Пришел сделать действие
[996700999999] :: Выполнили действие 1 с <таким-то> результатом
[996700999999] :: Не смогли выполнить действие 2 <потому-то>


SPR-905
Если используется структурное логирование, то описанные в предыдущих рекомендациях идентификаторы должны обязательно быть помещены в MDC (Mapped Diagnostic Context)

Критичность: Высокая

Цели/причины:

MDC позволяет автоматически добавлять идентификаторы во все логи в рамках потока выполнения
Упрощает структурированное логирование и обеспечивает единообразие формата
Улучшает возможности поиска и анализа логов в системах мониторинга


SPR-906
В промышленной среде рекомендуется использовать структурное логирование в формате JSON.

Критичность: Высокая

Цели/причины:

Позволяет просто искать и фильтровать логи по различным полям в Kibana
JSON-формат упрощает парсинг и анализ логов системами мониторинга и анализа
Улучшает интеграцию с системами сбора логов
При этом для локальной разработки и отладки допускается использование дефолтного формата логирования через консоль.

Это можно реализовать через переменные окружения:

application.yml

logging:
  structured:
    format:
      console: ${LOGGING_STRUCTURED_FORMAT_CONSOLE:}
Для production-окружения можно устанавливать LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs


SPR-907
Никогда не писать в логи полные значения ОТП, пароля, ключа и любых других данных являющихся частью секрета или фактора аутентификации.

Критичность: Блокирующая

Допускается логирование с маскированием всех символов, кроме первого и последнего при условии, что длина логируемого значения не короче 5 символов. Если короче - маскируем всё значение.

Это же относится и к логированию sensitive-данных в логах запросах к внешним rest-api.

Цели/причины:

Логирование подобных данных создает уязвимость безопасности, так как логи могут быть доступны в том числе и сопровождающему персоналу
Маскирование позволяет сохранить возможность отладки без раскрытия конфиденциальных данных
Предотвращает утечку секретов через системы мониторинга и анализа логов


SPR-908
Не рекомендуется логировать исключение в случаях, если мы его прокидываем дальше или оборачиваем в другое исключение с проставлением в cause

Критичность: Средняя

Цели/причины:

Логирование исключения перед пробросом приводит к дублированию логов в разных слоях приложения
Предотвращает засорение логов повторяющимися сообщениями и упрощает анализ ошибок

