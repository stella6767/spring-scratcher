package freeapp.me.flywayjooq.repository

import org.jooq.Condition
import org.jooq.Field
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL


fun <T : Any?> Condition.eqIfNotNull(field: Field<T>, value: T): Condition {
    if (value == null) return DSL.noCondition()
    return field.eq(value)
}


fun <R : Record?, T> TableField<R, T>.eqifNotNull(value: T?): Condition {
    if (value == null) return DSL.noCondition()
    return this.eq(value)
}
