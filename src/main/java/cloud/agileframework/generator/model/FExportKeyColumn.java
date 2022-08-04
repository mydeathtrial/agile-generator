package cloud.agileframework.generator.model;

import cloud.agileframework.generator.properties.AnnotationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import java.lang.annotation.Annotation;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FExportKeyColumn extends ColumnModel {
    private String pkName;
    private String fktableSchem;
    private String deferrability;
    private String deleteRule;
    private String pkcolumnName;
    private String pktableCat;
    private String fkName;
    private String fktableName;
    private String fktableCat;
    private String pktableSchem;
    private String pktableName;
    private String updateRule;
    private String keySeq;
    private String fkcolumnName;

    public void setFktableName(String fktableName) {
        this.fktableName = fktableName;
        setColumnName(fktableName + "_ids");

        setJavaType(List.class);
        setJavaSimpleTypeName("List<String>");
    }

    @Override
    public void build() {
        addAnnotation(new ElementCollection() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ElementCollection.class;
            }

            @Override
            public Class targetClass() {
                return void.class;
            }

            @Override
            public FetchType fetch() {
                return FetchType.EAGER;
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));

        ForeignKey foreignKey = new ForeignKey() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ForeignKey.class;
            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public ConstraintMode value() {
                return ConstraintMode.PROVIDER_DEFAULT;
            }

            @Override
            public String foreignKeyDefinition() {
                return "";
            }
        };

        addAnnotation(new CollectionTable() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CollectionTable.class;
            }

            @Override
            public String name() {
                return getFktableName();
            }

            @Override
            public String catalog() {
                return "";
            }

            @Override
            public String schema() {
                return "";
            }

            @Override
            public JoinColumn[] joinColumns() {
                return new JoinColumn[]{
                        new JoinColumn() {
                            @Override
                            public Class<? extends Annotation> annotationType() {
                                return JoinColumn.class;
                            }

                            @Override
                            public String name() {
                                return getFkcolumnName();
                            }

                            @Override
                            public String referencedColumnName() {
                                return getPkcolumnName();
                            }

                            @Override
                            public boolean unique() {
                                return false;
                            }

                            @Override
                            public boolean nullable() {
                                return true;
                            }

                            @Override
                            public boolean insertable() {
                                return true;
                            }

                            @Override
                            public boolean updatable() {
                                return true;
                            }

                            @Override
                            public String columnDefinition() {
                                return "";
                            }

                            @Override
                            public String table() {
                                return "";
                            }

                            @Override
                            public ForeignKey foreignKey() {
                                return foreignKey;
                            }
                        }
                };
            }

            @Override
            public ForeignKey foreignKey() {
                return foreignKey;
            }

            @Override
            public UniqueConstraint[] uniqueConstraints() {
                return new UniqueConstraint[0];
            }

            @Override
            public Index[] indexes() {
                return new Index[0];
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));

        addAnnotation(new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String name() {
                return getFktableName() + "_id";
            }

            @Override
            public boolean unique() {
                return false;
            }

            @Override
            public boolean nullable() {
                return true;
            }

            @Override
            public boolean insertable() {
                return true;
            }

            @Override
            public boolean updatable() {
                return true;
            }

            @Override
            public String columnDefinition() {
                return "";
            }

            @Override
            public String table() {
                return "";
            }

            @Override
            public int length() {
                return 255;
            }

            @Override
            public int precision() {
                return 0;
            }

            @Override
            public int scale() {
                return 0;
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));

        addAnnotation(new Fetch() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Fetch.class;
            }

            @Override
            public FetchMode value() {
                return FetchMode.SUBSELECT;
            }
        }, AnnotationType.HIBERNATE, desc -> getAnnotationDesc().add(desc));

        setMethod(getJavaName());
    }

    @Override
    public boolean isGeneric() {
        return false;
    }
}
