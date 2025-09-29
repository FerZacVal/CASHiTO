# Estructura MVVM - CASHiTO

## Organización de Carpetas

### 📁 Domain Layer (Lógica de Negocio)
```
domain/
├── entities/           # Modelos de datos del dominio
│   ├── expense/       # Entidades relacionadas con gastos
│   ├── savings/       # Entidades relacionadas con metas de ahorro
│   ├── report/        # Entidades relacionadas con reportes
│   ├── category/      # Entidades relacionadas con categorías
│   └── recommendation/ # Entidades relacionadas con recomendaciones de IA
├── usecases/          # Casos de uso (CU01-CU26)
│   ├── expense/       # Casos de uso de gastos (CU01-CU10)
│   ├── savings/       # Casos de uso de ahorros (CU11-CU18)
│   ├── report/        # Casos de uso de reportes (CU19-CU22)
│   ├── category/      # Casos de uso de categorías (CU06-CU09)
│   └── recommendation/ # Casos de uso de recomendaciones (CU23-CU26)
└── repositories/      # Interfaces de repositorios
```

### 📁 Data Layer (Acceso a Datos)
```
data/
├── datasources/       # Fuentes de datos
│   ├── firebase/      # Implementaciones de Firebase
│   └── local/         # Implementaciones locales (Room, SharedPreferences)
├── repositories/      # Implementaciones de repositorios
├── dto/              # Data Transfer Objects
└── mappers/          # Convertidores entre DTOs y entidades
```

### 📁 UI Layer (Interfaz de Usuario)
```
ui/
├── screens/          # Pantallas de la aplicación
│   ├── auth/         # Pantallas de autenticación
│   ├── home/         # Pantalla principal
│   ├── expense/      # Pantallas de gastos
│   ├── savings/      # Pantallas de metas de ahorro
│   ├── report/       # Pantallas de reportes
│   ├── category/     # Pantallas de categorías
│   └── recommendation/ # Pantallas de recomendaciones
├── components/       # Componentes reutilizables
│   └── stateholders/ # State holders
├── viewmodel/        # ViewModels
│   ├── auth/         # ViewModels de autenticación
│   ├── home/         # ViewModel principal
│   ├── expense/      # ViewModels de gastos
│   ├── savings/      # ViewModels de ahorros
│   ├── report/       # ViewModels de reportes
│   ├── category/     # ViewModels de categorías
│   └── recommendation/ # ViewModels de recomendaciones
└── theme/            # Temas y estilos
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

### 📁 Common Layer (Utilidades Comunes)
```
common/
├── utils/            # Utilidades generales
└── constants/        # Constantes de la aplicación
```

## Casos de Uso Mapeados

### Gastos (CU01-CU10)
- **CU01** - Registrar gasto
- **CU02** - Consultar gastos
- **CU03** - Consultar detalle de gasto
- **CU04** - Modificar gasto
- **CU05** - Eliminar gasto
- **CU10** - Buscar gasto

### Categorías (CU06-CU09)
- **CU06** - Registrar categoría personalizada
- **CU07** - Consultar categorías de gasto
- **CU08** - Modificar categoría de gasto
- **CU09** - Eliminar categoría de gasto

### Metas de Ahorro (CU11-CU18)
- **CU11** - Registrar meta de ahorro
- **CU12** - Consultar metas de ahorro
- **CU13** - Consultar detalle de meta de ahorro
- **CU14** - Modificar meta de ahorro
- **CU15** - Eliminar meta de ahorro
- **CU16** - Consultar progreso de meta de ahorro
- **CU17** - Recibir notificación de meta próxima a vencer
- **CU18** - Recibir notificación de meta alcanzada

### Reportes (CU19-CU22)
- **CU19** - Generar reporte financiero
- **CU20** - Consultar reporte financiero
- **CU21** - Descargar reporte financiero
- **CU22** - Compartir reporte financiero

### Recomendaciones de IA (CU23-CU26)
- **CU23** - Obtener recomendaciones de ahorro con IA
- **CU24** - Consultar recomendaciones de ahorro
- **CU25** - Valorar recomendación de ahorro
- **CU26** - Recibir notificación de nueva recomendación

## Principios de la Arquitectura

1. **Separación de responsabilidades**: Cada capa tiene una responsabilidad específica
2. **Inversión de dependencias**: La capa de dominio no depende de las otras capas
3. **Testabilidad**: Cada capa puede ser probada independientemente
4. **Mantenibilidad**: Código organizado y fácil de mantener
5. **Escalabilidad**: Estructura que permite agregar nuevas funcionalidades fácilmente
