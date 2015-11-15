#madura-vaadin-tableeditor#

Extended table editor that uses popup editor windows and an extension of Vaadin's JPAContainer addon that supports Spring's @Transactional.

Example configuration:

	@Bean(name="personTableLayout")
	@UIScope
	public TableEditorLayout<Person> getTableEditorLayout() {
		TableEditorLayout<Person> ret = new TableEditorLayout<Person>("people", Person.class);
		ret.setColumns(new String[]{"name","email","address","gender","startDate","amount"});
		return ret;
	}
	@Bean(name="editorWindow")
	@UIScope
	public EditorWindow<Person> getEditorWIndow() {
		EditorWindowImpl<Person> ret = new EditorWindowImpl<Person>("person",ValoTheme.BUTTON_PRIMARY);
		return ret;
	}

Also make sure Spring scans the {@code nz.co.senanque.vaadin.tableeditor} package. You also need to configure the JPAContainerEditorFactory.

	@Configuration
	@EnableJpaRepositories
	@EnableTransactionManagement
	@ComponentScan("nz.co.senanque.addressbook.jpa") // specifies where the domain objects are
	public class ConfigJPA {
	
		@Bean
		public DataSource dataSource() {
		
			EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
			return builder.setType(EmbeddedDatabaseType.H2).build();
		}
		
		@Bean
		public EntityManagerFactory entityManagerFactory() {
		
			HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
			vendorAdapter.setGenerateDdl(true);
			
			LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
			factory.setJpaVendorAdapter(vendorAdapter);
			factory.setDataSource(dataSource());
			factory.afterPropertiesSet();
			
			return factory.getObject();
		}
		
		@Bean
		public PlatformTransactionManager transactionManager() {
		
			JpaTransactionManager txManager = new JpaTransactionManager();
			txManager.setEntityManagerFactory(entityManagerFactory());
			return txManager;
			}
		}
	}

