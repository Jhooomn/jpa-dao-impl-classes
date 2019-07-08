/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.util.List;

/**
 *
 * @author Jhon Baron
 */
public interface IDao<T, ID> {
    public void crear (T entity);
    public <T> T consultar(Class<T> entityClass, Object primaryKey);
    public <T> T modificar(T entity);
    public void eliminar(Object entity);
    public List<T> consultarTodo(Class<T> entityClass);
    
}
