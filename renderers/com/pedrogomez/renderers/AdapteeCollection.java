/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedrogomez.renderers;

import java.util.Collection;

/**
 * Interface created to represent the adaptee collection used in RendererAdapter and
 * RVRendererAdapter. RendererAdapter and RVRendererAdapter will be created with a RendererBuilder
 * and an AdapteeCollection that store all the content to show in a ListView or RecyclerView
 * widget.
 *
 * This library provides a default implementation of AdapteeCollection named ListAdapteeCollection,
 * use it if needed or create your own AdapteeCollection implementations.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public interface AdapteeCollection<T> {

  /**
   * @return size of the adaptee collection.
   */
  int size();

  /**
   * Search an element using the index passed as argument.
   *
   * @param index to search in the collection.
   * @return the element stored at index passed as argument.
   */
  T get(int index);

  /**
   * Add a new element to the AdapteeCollection.
   *
   * @param element to add.
   * @return if the element was successfully added.
   */
  boolean add(T element);

  /**
   * Remove one element from the AdapteeCollection.
   *
   * @param  element to add.
   * @return if the element was successfully removed.
   */
  boolean remove(Object element);

  /**
   * Add a element collection to the AdapteeCollection.
   *
   * @param elements to add.
   * @return if the elements were successfully added.
   */
  boolean addAll(Collection<? extends T> elements);

  /**
   * Remove a element collection to the AdapteeCollection.
   *
   * @param elements to add.
   * @return if the elements were successfully removed.
   */
  boolean removeAll(Collection<?> elements);

  /**
   * Remove all element inside the AdapteeCollection.
   */
  void clear();
}
