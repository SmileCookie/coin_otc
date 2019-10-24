import React from 'react';
import PropTypes from 'prop-types';
import ScrollArea from 'react-scrollbar'
import './form.less';

class Select extends React.Component{
    static propTypes = {
        list: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string.isRequired,
                code: PropTypes.number.isRequired,
            })
        ).isRequired,
        currentCode: PropTypes.number.isRequired,
        getCode: PropTypes.func
    }
    static defaultProps = {
        getCode: (item = {}) => {

        } 
    }
    constructor(props){
        super(props);

        this.state = {
            tith: '',
            ck: 1,
            defultItem:''
        }

        this.getCode = this.getCode.bind(this);
        this.ins = this.ins.bind(this);
        this.out = this.out.bind(this);
    }

    componentWillMount(){
        const { getCode, list } = this.props;
        // getCode(list[1]);
    }

    ins(){
        this.setState({
            ck: 0,
        })
    }
    out(){
        this.setState({
            ck: 1
        });
    }
    getCode(e){
        this.setState({
            ck: 1,
            tith: e.name,
        });
        this.props.getCode(e);
    }
    componentDidMount(){
        const props = this.props;
        const tith = props.list.filter(v => {
            return v.code === props.currentCode;
        })[0].name;

        const defultItem = props.list.filter(v => {
            return v.code === props.currentCode;
        })[0];
        this.setState({
            tith,
            defultItem
        },() => props.getCode(this.state.defultItem));
    }
    render(){
        const { tith, ck } = this.state;
        const { list } = this.props;
        const { ins, out } = this;
        return (
            <div className="selwp">
                <div onMouseEnter={ins} onMouseLeave={out} className="stithwp">
                    <strong className="stith">{tith}</strong><em className="iconfont">&#xe681;</em>
                    <div className="country">
                        <div className={`${ck ? 'hide' : ''} goog-menu`}>
                            <ScrollArea  className="goog-menu-con scrollarea trade-scrollarea scrollarea-content">
                            {
                                list.map(v => {
                                    
                                    return (
                                        <div  onClick={() => {this.getCode(v)}} key={v.code} className="goog-menuitem">
                                            <div className="goog-menuitem-content">
                                                    <div className="goog-inline-block">
                                                        <div className="talk-flag"></div>
                                                    </div>
                                                    <span className="talk-select-country-name">{v.name}</span> 
                                            </div>
                                        </div>
                                    )
                                })
                            }
                            </ScrollArea>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default Select;