package com.fuelac.fuelac.repository.specification;

import com.fuelac.fuelac.dto.search.FilterRequest;
import com.fuelac.fuelac.dto.search.Operator;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GenericSpecification {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public <T> Specification<T> getSpecification(List<FilterRequest> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }
            List<Predicate> andPredicates = new ArrayList<>();
            Map<String, List<FilterRequest>> orGroups = new LinkedHashMap<>();

            for (FilterRequest filter : filters) {
                String group = filter.getOrGroup();
                if (group != null && !group.isEmpty()) {
                    orGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(filter);
                } else {
                    andPredicates.add(buildPredicate(root, cb, filter));
                }
            }

            for (List<FilterRequest> group : orGroups.values()) {
                Predicate[] orPredicates = group.stream()
                        .map(f -> buildPredicate(root, cb, f))
                        .toArray(Predicate[]::new);
                andPredicates.add(cb.or(orPredicates));
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Predicate buildPredicate(Root<T> root, CriteriaBuilder cb, FilterRequest filter) {
        String key = filter.getKey();
        Operator operator = filter.getOperator();
        Object value = filter.getValue();
        Object valueTo = filter.getValueTo();

        Path<?> path = resolvePath(root, key);
        // Используем прямой Java-каст вместо path.as(Comparable.class),
        // чтобы избежать ограничений Hibernate 6 на преобразование типов.
        Expression<Comparable> comparablePath = (Expression<Comparable>) path;

        switch (operator) {
            case EQUAL:
                return cb.equal(path, convertValue(path.getJavaType(), value));
            case NOT_EQUAL:
                return cb.notEqual(path, convertValue(path.getJavaType(), value));
            case LIKE:
                return cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%");
            case GT:
                return cb.greaterThan(comparablePath, (Comparable) convertValue(path.getJavaType(), value));
            case GTE:
                return cb.greaterThanOrEqualTo(comparablePath, (Comparable) convertValue(path.getJavaType(), value));
            case LT:
                return cb.lessThan(comparablePath, (Comparable) convertValue(path.getJavaType(), value));
            case LTE:
                return cb.lessThanOrEqualTo(comparablePath, (Comparable) convertValue(path.getJavaType(), value));
            case BETWEEN:
                return cb.between(comparablePath,
                        (Comparable) convertValue(path.getJavaType(), value),
                        (Comparable) convertValue(path.getJavaType(), valueTo));
            case IN:
                return path.in((List<?>) value);
            default:
                throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Path<?> resolvePath(Root<T> root, String key) {
        if (!key.contains(".")) {
            return root.get(key);
        }
        String[] parts = key.split("\\.");
        // Если первый сегмент — @Embedded-атрибут, используем навигацию по пути (без JOIN).
        try {
            jakarta.persistence.metamodel.Attribute<?, ?> firstAttr = root.getModel().getAttribute(parts[0]);
            if (firstAttr.getPersistentAttributeType()
                    == jakarta.persistence.metamodel.Attribute.PersistentAttributeType.EMBEDDED) {
                Path<Object> p = root.get(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    p = p.get(parts[i]);
                }
                return p;
            }
        } catch (Exception ignored) {}
        // Для ассоциаций (ManyToOne, OneToMany…) используем LEFT JOIN.
        Join<?, ?> join = root.join(parts[0], JoinType.LEFT);
        for (int i = 1; i < parts.length - 1; i++) {
            join = join.join(parts[i], JoinType.LEFT);
        }
        return join.get(parts[parts.length - 1]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object convertValue(Class<?> type, Object value) {
        if (value == null) return null;
        String str = value.toString();
        if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(str);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(str);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(str);
        } else if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(str, DATE_FORMATTER);
        } else if (type.equals(LocalDate.class)) {
            return LocalDate.parse(str);
        } else if (type.equals(UUID.class)) {
            return UUID.fromString(str);
        } else if (type.isEnum()) {
            // Try Java constant name first (e.g. OPEN, CLOSED)
            try {
                return Enum.valueOf((Class<Enum>) type, str);
            } catch (IllegalArgumentException e) {
                // Fallback: match by display name (e.g. Открыт, Закрыт)
                for (Enum constant : ((Class<Enum>) type).getEnumConstants()) {
                    try {
                        java.lang.reflect.Method m = type.getMethod("getDisplayName");
                        if (str.equalsIgnoreCase(m.invoke(constant).toString())) {
                            return constant;
                        }
                    } catch (Exception ignored) {
                    }
                }
                throw new IllegalArgumentException("Неизвестное значение перечисления: " + str + " для " + type.getSimpleName());
            }
        }
        return str;
    }
}
