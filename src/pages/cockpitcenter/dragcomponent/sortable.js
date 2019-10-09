import React, { Component } from 'react';

import {
  SortableContainer,
  SortableElement,
  SortableHandle,
} from 'react-sortable-hoc';


import GridLayout from './gridLayout'

const DragHandle = SortableHandle(() => <span className='DragHandle'>:拖拽这里移动:</span>);

const SortableItem = SortableElement(({ value, index, fakeData, onLayoutChange,onDelete,isDraggable,isResizable,_this }) => <li style={{ padding: "14px", border: "1px solid #EAEEF2", marginBottom: '10px', backgroundColor: '#fff', boxShadow: '0 2px 4px 0 rgba(234,236,239,0.70)' }}>
  {isDraggable ? <DragHandle /> :''}
  <span style={{ fontWeight: '600', fontSize: '14px',paddingLeft:'12px' }}>{value.name}</span><a style={{marginLeft:'10px'}} href='javascript:void(0);' onClick={() => _this.add(value)}>查看更多</a>
  <GridLayout layoutsName={value.url} layouts={value.layouts} onLayoutChange={onLayoutChange} onDelete={onDelete} layoutsIndex={index} fakeData={value.fakeData} isDraggable={isDraggable} isResizable={isResizable} />
</li>);

const SortableList = SortableContainer(({ items, fakeData, onLayoutChange,onDelete,isDraggable,isResizable,_this }) => {
  return (
    <ul style={{ padding: '0' }}>
      {items.map((value, index) => (
        <SortableItem key={`item-${index}`} _this={_this} onLayoutChange={onLayoutChange} onDelete={onDelete} fakeData={fakeData} index={index} value={value} isDraggable={isDraggable} isResizable={isResizable} />
      ))}
    </ul>
  );
});

class SortableComponent extends Component {
  constructor(props) {
    super(props)
    this.state = {
      items: [],
      fakeData: [],
      isDraggable:false,
      isResizable:false
    }
  }
  componentDidMount() {
    // console.log(this.props)
    const { statisticsItems, fakeData,isDraggable,isResizable } = this.props
    this.setState({
      items: statisticsItems,
      fakeData,
      isDraggable,
      isResizable
    })
  }
  componentWillReceiveProps(nextProps) {
    const { statisticsItems, fakeData,isDraggable,isResizable } = nextProps
    this.setState({
      items: statisticsItems,
      fakeData,
      isDraggable,
      isResizable
    })
  }
  onSortEnd = ({ oldIndex, newIndex }) => {
    // this.setState(({items}) => ({
    //   items: arrayMove(items, oldIndex, newIndex),
    // }));
    this.props.onSortEnd(oldIndex, newIndex)
  };
  render() {
    // console.log(this.props)
    const { items, fakeData,isDraggable,isResizable } = this.state
    return <SortableList 
        items={items}
        onLayoutChange={this.props.onLayoutChange}
        fakeData={fakeData} onSortEnd={this.onSortEnd}
        onDelete={this.props.onDelete} 
        isDraggable={isDraggable}
        isResizable={isResizable}
        useDragHandle
        disable={false}
        _this={this.props._this}
        />;
  }
}


export default SortableComponent;
